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

package org.apache.daffodil.dsom

import org.apache.daffodil.dpath._
import scala.xml.NamespaceBinding
import org.apache.daffodil.xml.NamedQName
import java.lang.{ Long => JLong, Boolean => JBoolean }
import org.apache.daffodil.schema.annotation.props.Found
import org.apache.daffodil.oolag.OOLAG._

object ExpressionCompilers extends ExpressionCompilerClass {
  override val String = new ExpressionCompiler[String]
  override val JLong = new ExpressionCompiler[JLong]
  override val AnyRef = new ExpressionCompiler[AnyRef]
  override val JBoolean = new ExpressionCompiler[JBoolean]
}

class ExpressionCompiler[T <: AnyRef] extends ExpressionCompilerBase[T] {

  /**
   * Compiles the expression.
   *
   * This method available at compilation and also at runtime for use by the debuggger.
   *
   * Expression may or may not have braces around it. It could still be a constant as in
   * { 5 } or { "California" }, and in that case this should return a ConstantExpression
   * object.
   */
  def compileExpression(qn: NamedQName, nodeInfoKind: NodeInfo.Kind, exprWithBracesMaybe: String, namespaces: NamespaceBinding,
    compileInfoWherePropertyWasLocated: DPathCompileInfo,
    isEvaluatedAbove: Boolean, host: OOLAGHost): CompiledExpression[T] = {
    val (exprForCompiling, isRealExpression) = exprOrLiteral(exprWithBracesMaybe, nodeInfoKind, compileInfoWherePropertyWasLocated)
    compileExpression1(qn, nodeInfoKind, exprForCompiling, namespaces, compileInfoWherePropertyWasLocated, isEvaluatedAbove, host,
      isRealExpression)
  }

  /**
   * Compile a potentially runtime-valued property.
   *
   * The property value can be an expression or a literal constant.
   *
   * This form for expressions that are the values of most DFDL properties
   *
   * The isEvaluatedAbove argument is used for properties like occursCount
   * which are evaluated before the element whose declaration carries it, exists.
   * That is, it is evaluated one Infoset node above where the expression is
   * written. This doesn't matter unless the expression contains relative
   * paths. In that case those relative paths will all have to be adjusted
   * so they work in the context of one node above.
   */
  def compileProperty(qn: NamedQName, nodeInfoKind: NodeInfo.Kind, property: Found, host: OOLAGHost, isEvaluatedAbove: Boolean = false): CompiledExpression[T] =
    compileExpression(qn,
      nodeInfoKind,
      property.value,
      property.location.namespaces,
      propertyCompileInfo(property),
      isEvaluatedAbove, host)

  /**
   * Compile a potentially runtime-valued delimiter property
   *
   * The property value can be an expression or a literal constant.
   *
   * This form for delimiters and escapeEscapeCharacter since they
   * can have empty string if statically known, but if an evaluated expression,
   * it must be non-empty string.
   *
   * You can have an empty string, but only statically.
   * That turns off separators entirely.
   * If you have an expression (that is not trivially an empty string),
   * then it must be a non-empty string as the compiled parser will be
   * generated assuming there will be a concrete separator that is part of
   * the data syntax and serves as any delimiter to anchor the parse algorithm.
   *
   * We don't want to allow turning on/off whether a format is delimited or
   * not based on runtime expressions, only what the delimiters are.
   */
  def compileDelimiter(qn: NamedQName, staticNodeInfoKind: NodeInfo.Kind, runtimeNodeInfoKind: NodeInfo.Kind, property: Found,
    host: OOLAGHost): CompiledExpression[T] = {
    val isEvaluatedAbove = false
    val expr = property.value
    val namespacesForNamespaceResolution = property.location.namespaces
    val compileInfoWherePropertyWasLocated = propertyCompileInfo(property)
    val (exprForCompiling, isRealExpression) = exprOrLiteral(expr, staticNodeInfoKind, compileInfoWherePropertyWasLocated)
    val compiled1 = compileExpression1(qn, staticNodeInfoKind, exprForCompiling, namespacesForNamespaceResolution, compileInfoWherePropertyWasLocated, isEvaluatedAbove, host,
      isRealExpression)
    if (compiled1.isConstant) return compiled1
    if (staticNodeInfoKind == runtimeNodeInfoKind) return compiled1
    //
    // TODO: consider passing in a flag or some other way of avoiding this
    // duplicate compile run.

    // This is, this nodeInfo.Kind is used as the target type in the DPath expression compiler, and
    //
    val compiled2 = compileExpression1(qn, runtimeNodeInfoKind, exprForCompiling, namespacesForNamespaceResolution, compileInfoWherePropertyWasLocated,
      isEvaluatedAbove, host, isRealExpression)
    compiled2
  }

  /**
   * Returns compile info of property regardless of origin.
   *
   * Needed because before serialization of the runtime data
   * objects, the location of a property may be the associated schema
   * component. Once serialized, at runtime when we're compiling expressions
   * we have only the DPathCompileInfo.
   */
  private def propertyCompileInfo(property: Found) = {
    val compileInfoWherePropertyWasLocated = {
      property.location match {
        case sc: SchemaComponent => sc.dpathCompileInfo
        case di: DPathCompileInfo => di
      }
    }
    compileInfoWherePropertyWasLocated
  }

  /**
   * Returns expression and flag as to whether it must be compiled.
   *
   * If the 2nd return value, 'isRealExpression' is true then we need to compile
   * the expression, and the expression *will* have curly braces around it.
   * If the 2nd return value is false, then the expression is a string literal
   * being evaluated for a string, so we can directly construct a constant
   * expression object.
   */
  private def exprOrLiteral(exprWithBracesMaybe: String, nodeInfoKind: NodeInfo.Kind, compileInfoWherePropertyWasLocated: DPathCompileInfo) = {
    var compile: Boolean = true
    val expr = exprWithBracesMaybe
    //
    // we want to standardize that the expression has braces
    //
    val exprForCompiling =
      if (DPathUtil.isExpression(expr)) expr.trim
      else {
        // not an expression. For some properties like delimiters, you can use a literal string
        // whitespace separated list of literal strings, or an expression in { .... }
        if (expr.startsWith("{") && !expr.startsWith("{{")) {
          val msg = "'%s' is an unterminated expression.  Add missing closing brace, or escape opening brace with another opening brace."
          compileInfoWherePropertyWasLocated.SDE(msg, expr)
        }
        val expr1 = if (expr.startsWith("{{"))
          expr.tail // everything except the self-escaped leading brace
        else expr
        //
        // Literal String: if the target type is String, do not compile.
        //
        nodeInfoKind match {
          case _: NodeInfo.String.Kind => compile = false // Constant String
          case _ => compile = true
        }
        expr1
      }

    // If we get here then now it's something we can compile. It might be trivial
    // to compile (e.g, '5' compiles to Literal(5)) but we no longer uniformly
    // compile everything.  As a performance optimization (DFDL-1775),
    // we will NOT compile constant strings (constant values whose target type
    // is String).

    /* Question: If something starts with {{, e.g.
     * separator="{{ not an expression", then we strip off the first brace,
     * wrap in quotes, and compile it? Why try compiling it? Shouldn't we just
     * return a constant expression or something at this point?
     * <p>
     * Answer: Conversions. E.g., if you have "{{ 6.847 }" as the expression
     * for an inputValueCalc on an element of float type, then the compiler
     * can tell you this isn't going to convert - you get a type check error or
     * maybe a number format exception at constant-folding time, which tells us
     * that the expression - even though it's a constant, isn't right.
     *
     * If we try to do this outside the expression compiler we'd be replicating
     * some of this type-infer/check logic.
     */
    (exprForCompiling, compile)
  }

  /**
   * Compile the expression or construct a constant expression from it.
   */
  private def compileExpression1(qn: NamedQName, nodeInfoKind: NodeInfo.Kind, exprForCompiling: String, namespaces: NamespaceBinding,
    compileInfoWherePropertyWasLocated: DPathCompileInfo,
    isEvaluatedAbove: Boolean, host: OOLAGHost, isRealExpression: Boolean): CompiledExpression[T] = {
    val res = if (isRealExpression) {
      // This is important. The namespace bindings we use must be
      // those from the object where the property carrying the expression
      // was written, not those of the edecl object where the property
      // value is being used/compiled. JIRA DFDL-407
      //
      val compiler = new DFDLPathExpressionParser[T](qn,
        nodeInfoKind, namespaces, compileInfoWherePropertyWasLocated, isEvaluatedAbove, host)
      val compiledDPath = compiler.compile(exprForCompiling)
      compiledDPath
    } else {
      // Don't compile, meaning this is a constant string
      new ConstantExpression[T](qn, nodeInfoKind, exprForCompiling.asInstanceOf[T])
    }
    res
  }
}
