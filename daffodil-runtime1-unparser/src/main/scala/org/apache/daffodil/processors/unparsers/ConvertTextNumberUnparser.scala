/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.daffodil.processors.unparsers

import java.lang.{ Double => JDouble }
import java.lang.{ Float => JFloat }

import org.apache.daffodil.processors._
import org.apache.daffodil.util.Maybe
import org.apache.daffodil.util.Maybe._
import org.apache.daffodil.cookers.EntityReplacer
import org.apache.daffodil.exceptions.Assert
import org.apache.daffodil.processors.parsers.NumberFormatFactoryBase
import org.apache.daffodil.processors.parsers.ConvertTextNumberParserUnparserHelperBase

case class ConvertTextCombinatorUnparser(
  rd: TermRuntimeData,
  valueUnparser: Unparser,
  converterUnparser: Unparser)
  extends CombinatorUnparser(rd) {

  override lazy val runtimeDependencies = Vector()

  override lazy val childProcessors = Vector(converterUnparser, valueUnparser)

  override def unparse(state: UState): Unit = {
    converterUnparser.unparse1(state)

    if (state.processorStatus ne Success) return

    valueUnparser.unparse1(state)
  }
}

case class ConvertTextNumberUnparser[S](
  helper: ConvertTextNumberParserUnparserHelperBase[S],
  nff: NumberFormatFactoryBase[S],
  override val context: ElementRuntimeData)
  extends PrimUnparser
  with ToBriefXMLImpl {

  override lazy val runtimeDependencies = Vector()

  override def toString = "to(xs:" + helper.xsdType + ")"
  override lazy val childProcessors = Vector()

  lazy val zeroRep: Maybe[String] = helper.zeroRepListRaw.headOption.map { zr =>
    EntityReplacer { _.replaceForUnparse(zr) }
  }

  override def unparse(state: UState): Unit = {

    val node = state.currentInfosetNode.asSimple
    val value = node.dataValue

    // The type of value should have the type of S, but type erasure makes this
    // difficult to assert. Could probably check this with TypeTags or Manifest
    // if we find this is not the case. Want something akin to:
    // Assert.invariant(value.isInstanceOf[S])
        
    val df = nff.getNumFormat(state).get
    val dfs = df.getDecimalFormatSymbols

    val strRep = value match {
      case n: Number if n == 0 && zeroRep.isDefined => zeroRep.get
      // We need to special case infinity and NaN because ICU4J has a bug and
      // will add an exponent to inf/nan (e.g. INFx10^0) if defined in the
      // pattern, which we don't want. We need to manually output the inf/nan
      // rep plus the prefix and suffix
      case f: JFloat if f.isInfinite =>
        if (f > 0) df.getPositivePrefix + dfs.getInfinity + df.getPositiveSuffix
        else df.getNegativePrefix + dfs.getInfinity + df.getNegativeSuffix
      case f: JFloat if f.isNaN => dfs.getNaN
      case d: JDouble if d.isInfinite =>
        if (d > 0) df.getPositivePrefix + dfs.getInfinity + df.getPositiveSuffix
        else df.getNegativePrefix + dfs.getInfinity + df.getNegativeSuffix
      case d: JDouble if d.isNaN => dfs.getNaN
      // Needed because the DecimalFormat class of ICU will call
      // doubleValue on scala's BigInt and BigDecimal because it
      // doesn't recognize it as Java's BigInteger and BigDecimal.
      // This caused large numbers to be truncated silently.
      case bd: scala.math.BigDecimal => Assert.usageError("Received scala.math.BigDecimal, expected java.math.BigDecimal.")
      case bi: scala.math.BigInt => Assert.usageError("Received scala.math.BigInt, expected java.math.BigInteger.")
      case _ =>
        try {
          df.format(value)
        } catch {
          case e: java.lang.ArithmeticException => UE(state, "Unable to format number to pattern: %s", e.getMessage())
        }
      }

    node.overwriteDataValue(strRep)
  }
}
