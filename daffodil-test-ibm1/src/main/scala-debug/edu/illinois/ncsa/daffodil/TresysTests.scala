package edu.illinois.ncsa.daffodil

/* Copyright (c) 2013 Tresys Technology, LLC. All rights reserved.
 *
 * Developed by: Tresys Technology, LLC
 *               http://www.tresys.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal with
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimers.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimers in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  3. Neither the names of Tresys Technology, nor the names of its contributors
 *     may be used to endorse or promote products derived from this Software
 *     without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 * SOFTWARE.
 */

import junit.framework.Assert._
import edu.illinois.ncsa.daffodil.xml.XMLUtils._
import scala.xml._
import edu.illinois.ncsa.daffodil.tdml.DFDLTestSuite
import edu.illinois.ncsa.daffodil.util.Misc
import org.junit.Test
import org.junit.Test

class TresysTestsDebug {

  // Debug Template
  // @Test def test_name() = Debugger.withDebugger { 
  // LoggingDefaults.setLoggingLevel(LogLevel.Debug)
  // runner.runOneTest("test_name") 
  // }

  val testDir = "/test-suite/tresys-contributed/"

  val delimited = testDir + "dpaext1.tdml"
  lazy val runnerDelimited = new DFDLTestSuite(Misc.getRequiredResource(delimited))

  @Test def test_length_delimited_12_03_controversial() { runnerDelimited.runOneTest("length_delimited_12_03_controversial") }

  val td = testDir + "multiple-diagnostics.tdml"
  lazy val runnerMD = new DFDLTestSuite(Misc.getRequiredResource(td), validateTDMLFile = true, validateDFDLSchemas = false)
  runnerMD.setCheckAllTopLevel(true)

  // AX debugged. Uses escape schemes. 
  val ax = testDir + "AX.tdml"
  lazy val runnerAX = new DFDLTestSuite(Misc.getRequiredResource(ax))
  @Test def test_AX000() = { runnerAX.runOneTest("AX000") } // escape schemes

  val ay = testDir + "AY.tdml"
  lazy val runnerAY = new DFDLTestSuite(Misc.getRequiredResource(ay))
  @Test def test_AY000() { runnerAY.runOneTest("AY000") } // escape schemes

}