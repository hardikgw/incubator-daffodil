package edu.illinois.ncsa.daffodil.section13.text_number_props

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
import org.junit.Test
import scala.xml._
import edu.illinois.ncsa.daffodil.xml.XMLUtils
import edu.illinois.ncsa.daffodil.xml.XMLUtils._
import edu.illinois.ncsa.daffodil.compiler.Compiler
import edu.illinois.ncsa.daffodil.util._
import edu.illinois.ncsa.daffodil.tdml.DFDLTestSuite
import java.io.File

class TestTextNumberProps {
  val testDir = "/edu/illinois/ncsa/daffodil/section13/text_number_props/"
  val aa = testDir + "TextNumberProps.tdml"
  lazy val runner = new DFDLTestSuite(Misc.getRequiredResource(aa))

  @Test def test_textNumberPattern_positiveMandatory() { runner.runOneTest("textNumberPattern_positiveMandatory") }
  @Test def test_textNumberPattern_negativeOptional() { runner.runOneTest("textNumberPattern_negativeOptional") }

// DFDL-846
//  @Test def test_textNumberPattern_negativeIgnored01() { runner.runOneTest("textNumberPattern_negativeIgnored01") }
//  @Test def test_textNumberPattern_negativeIgnored02() { runner.runOneTest("textNumberPattern_negativeIgnored02") }
//  @Test def test_textNumberPattern_negativeIgnored03() { runner.runOneTest("textNumberPattern_negativeIgnored03") }
//  @Test def test_textNumberPattern_negativeIgnored05() { runner.runOneTest("textNumberPattern_negativeIgnored05") }

  @Test def test_textNumberPattern_negativeIgnored04() { runner.runOneTest("textNumberPattern_negativeIgnored04") }
  @Test def test_textNumberPattern_negativeIgnored06() { runner.runOneTest("textNumberPattern_negativeIgnored06") }

  @Test def test_textNumberPattern_exponent01() { runner.runOneTest("textNumberPattern_exponent01") }
  @Test def test_textNumberPattern_specialChar01() { runner.runOneTest("textNumberPattern_specialChar01") }
  @Test def test_textNumberPattern_specialChar02() { runner.runOneTest("textNumberPattern_specialChar02") }
  @Test def test_textNumberPattern_specialChar03() { runner.runOneTest("textNumberPattern_specialChar03") }
  @Test def test_textNumberPattern_specialChar04() { runner.runOneTest("textNumberPattern_specialChar04") }
  
// DFDL-845
//  @Test def test_textNumberCheckPolicy_lax01() { runner.runOneTest("textNumberCheckPolicy_lax01") }
//  @Test def test_textNumberCheckPolicy_lax05() { runner.runOneTest("textNumberCheckPolicy_lax05") }
//  @Test def test_textNumberCheckPolicy_lax04() { runner.runOneTest("textNumberCheckPolicy_lax04") }
//  @Test def test_textNumberCheckPolicy_lax10() { runner.runOneTest("textNumberCheckPolicy_lax10") }
//  @Test def test_textNumberCheckPolicy_lax11() { runner.runOneTest("textNumberCheckPolicy_lax11") }
//  @Test def test_textNumberCheckPolicy_lax12() { runner.runOneTest("textNumberCheckPolicy_lax12") }
//  @Test def test_textNumberCheckPolicy_lax13() { runner.runOneTest("textNumberCheckPolicy_lax13") }
//  @Test def test_textNumberCheckPolicy_lax14() { runner.runOneTest("textNumberCheckPolicy_lax14") }
//  @Test def test_textNumberCheckPolicy_lax15() { runner.runOneTest("textNumberCheckPolicy_lax15") }
//  @Test def test_textNumberCheckPolicy_lax16() { runner.runOneTest("textNumberCheckPolicy_lax16") }

  @Test def test_textNumberCheckPolicy_lax02() { runner.runOneTest("textNumberCheckPolicy_lax02") }
  @Test def test_textNumberCheckPolicy_lax03() { runner.runOneTest("textNumberCheckPolicy_lax03") }
  @Test def test_textNumberCheckPolicy_lax06() { runner.runOneTest("textNumberCheckPolicy_lax06") }
  @Test def test_textNumberCheckPolicy_lax07() { runner.runOneTest("textNumberCheckPolicy_lax07") }
  @Test def test_textNumberCheckPolicy_lax08() { runner.runOneTest("textNumberCheckPolicy_lax08") }
  @Test def test_textNumberCheckPolicy_lax09() { runner.runOneTest("textNumberCheckPolicy_lax09") }
  @Test def test_textNumberCheckPolicy_lax17() { runner.runOneTest("textNumberCheckPolicy_lax17") }

  @Test def test_textNumberCheckPolicy_strict01() { runner.runOneTest("textNumberCheckPolicy_strict01") }
  @Test def test_textNumberCheckPolicy_strict02() { runner.runOneTest("textNumberCheckPolicy_strict02") }
  @Test def test_textNumberCheckPolicy_strict03() { runner.runOneTest("textNumberCheckPolicy_strict03") }
  @Test def test_textNumberCheckPolicy_strict04() { runner.runOneTest("textNumberCheckPolicy_strict04") }
  @Test def test_textNumberCheckPolicy_strict05() { runner.runOneTest("textNumberCheckPolicy_strict05") }
  @Test def test_textNumberCheckPolicy_strict06() { runner.runOneTest("textNumberCheckPolicy_strict06") }
  @Test def test_textNumberCheckPolicy_strict07() { runner.runOneTest("textNumberCheckPolicy_strict07") }
  
  @Test def test_textStandardDecimalSeparator01() { runner.runOneTest("textStandardDecimalSeparator01") }
  @Test def test_textStandardDecimalSeparator02() { runner.runOneTest("textStandardDecimalSeparator02") }
  @Test def test_textStandardDecimalSeparator03() { runner.runOneTest("textStandardDecimalSeparator03") }
  @Test def test_textStandardDecimalSeparator04() { runner.runOneTest("textStandardDecimalSeparator04") }
  @Test def test_textStandardDecimalSeparator05() { runner.runOneTest("textStandardDecimalSeparator05") }
  @Test def test_textStandardDecimalSeparator06() { runner.runOneTest("textStandardDecimalSeparator06") }
  @Test def test_textStandardDecimalSeparator07() { runner.runOneTest("textStandardDecimalSeparator07") }
  @Test def test_textStandardDecimalSeparator08() { runner.runOneTest("textStandardDecimalSeparator08") }
  @Test def test_textStandardDecimalSeparator09() { runner.runOneTest("textStandardDecimalSeparator09") }
  @Test def test_textStandardDecimalSeparator12() { runner.runOneTest("textStandardDecimalSeparator12") }
  @Test def test_textStandardDecimalSeparator13() { runner.runOneTest("textStandardDecimalSeparator13") }
  @Test def test_textStandardDecimalSeparator14() { runner.runOneTest("textStandardDecimalSeparator14") }
  @Test def test_textStandardDecimalSeparator15() { runner.runOneTest("textStandardDecimalSeparator15") }
  @Test def test_textStandardDecimalSeparator16() { runner.runOneTest("textStandardDecimalSeparator16") }

// DFDL-847
//  @Test def test_textStandardDecimalSeparator10() { runner.runOneTest("textStandardDecimalSeparator10") }
//  @Test def test_textStandardDecimalSeparator11() { runner.runOneTest("textStandardDecimalSeparator11") }

// DFDL-843
//  @Test def test_textStandardGroupingSeparator03() { runner.runOneTest("textStandardGroupingSeparator03") }
  
  @Test def test_textStandardGroupingSeparator01() { runner.runOneTest("textStandardGroupingSeparator01") }
  @Test def test_textStandardGroupingSeparator02() { runner.runOneTest("textStandardGroupingSeparator02") }
  @Test def test_textStandardGroupingSeparator04() { runner.runOneTest("textStandardGroupingSeparator04") }
  @Test def test_textStandardGroupingSeparator05() { runner.runOneTest("textStandardGroupingSeparator05") }
  @Test def test_textStandardGroupingSeparator06() { runner.runOneTest("textStandardGroupingSeparator06") }
  @Test def test_textStandardGroupingSeparator07() { runner.runOneTest("textStandardGroupingSeparator07") }
  @Test def test_textStandardGroupingSeparator08() { runner.runOneTest("textStandardGroupingSeparator08") }
  @Test def test_textStandardGroupingSeparator09() { runner.runOneTest("textStandardGroupingSeparator09") }
  @Test def test_textStandardGroupingSeparator10() { runner.runOneTest("textStandardGroupingSeparator10") }
  @Test def test_textStandardGroupingSeparator11() { runner.runOneTest("textStandardGroupingSeparator11") }
  @Test def test_textStandardGroupingSeparator13() { runner.runOneTest("textStandardGroupingSeparator13") }
//  @Test def test_textStandardGroupingSeparator14() { runner.runOneTest("textStandardGroupingSeparator14") }
//  @Test def test_textStandardGroupingSeparator15() { runner.runOneTest("textStandardGroupingSeparator15") }
//  @Test def test_textStandardGroupingSeparator16() { runner.runOneTest("textStandardGroupingSeparator16") }
//  @Test def test_textStandardGroupingSeparator17() { runner.runOneTest("textStandardGroupingSeparator17") }

// DFDL-851
//  @Test def test_textStandardGroupingSeparator12() { runner.runOneTest("textStandardGroupingSeparator12") }
//  @Test def test_textStandardDecimalSeparator17() { runner.runOneTest("textStandardDecimalSeparator17") }
  
  @Test def test_dynamic() { runner.runOneTest("dynamic") }
  @Test def test_dynamic_neg1() { runner.runOneTest("dynamicNeg1") }
  @Test def test_dynamic_neg2() { runner.runOneTest("dynamicNeg2") }

}
