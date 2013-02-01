package edu.illinois.ncsa.daffodil.grammar

import junit.framework.Assert._
import org.scalatest.junit.JUnitSuite
import org.junit.Test

/**
 * Scala Unit Testing Notes:
 *
 * It is important that the Eclipse IDE make it convenient to run the unit tests, step the user directly to the point
 * of failure, etc.
 *
 * Scalatest doesn't do this directly, but using it driven by JUnit does.
 *
 * So I'm advocating that a much more vanilla approach be taken to unit tests. Straight use of Junit.
 *
 * Here is an example. Some simple tests, some that intercept exceptions, and demonstrate that the intercept
 * device works properly.
 */
class HowToUseJUnit extends JUnitSuite {

  @Test def test() {
    assertEquals(42, 6 * 7)
    assertTrue(42 == 6 * 7)
    if (42 != 6 * 7)
      fail("wrong answer")
  }

  def somethingThatThrows() {
    throw new NumberFormatException()
  }

  @Test def testOfInterceptToTestExpectedThrows() {
    intercept[NumberFormatException] {
      //println("here we are")
      somethingThatThrows()
    }
  }

  // @Test
  def testOfInterceptReturnedValue() {
    val nfe = intercept[NumberFormatException] {
      //println("here we are")
      somethingThatThrows()
    }
    if (!nfe.getClass().getName().contains("NumberFormatException"))
      fail("object returned from intercept not the right one.")
  }

  // @Test
  //  def testOfInterceptToTestExpectedThrowsButItThrewSomethingElse() {
  //   val e = intercept[JUnitTestFailedError] { // FIXME: Not right exception to catch...
  //      intercept[NumberFormatException] { // won't get this one, so it will throw 
  //        //println("there we go")
  //        throw new Exception("foobar")
  //      }
  //    }
  //   if (!e.getMessage().contains("foobar")) 
  //     fail("didn't propagate unintercepted throw properly.")
  //  }

  // @Test
  //  def testOfInterceptToTestExpectedThrowsButItDidntThrowAtAll() {
  //    try {
  //      intercept[NumberFormatException] {
  //        // don't throw anything.
  //        // println("not going to throw")
  //      }
  //    } catch {
  //      case e: JUnitTestFailedError => // we're good // FIXME: wrong exception to catch
  //      case e => throw e
  //    }
  //  }

}