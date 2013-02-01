
package edu.illinois.ncsa.daffodil.grammar

import junit.framework.Assert._
import org.scalatest.junit.JUnitSuite
import edu.illinois.ncsa.daffodil.Implicits._
import edu.illinois.ncsa.daffodil.dsom._
import edu.illinois.ncsa.daffodil.exceptions.Assert
import edu.illinois.ncsa.daffodil.processors._
import org.junit.Test

class TestGrammar extends JUnitSuite {

  val fakeTerm = new GlobalElementDeclFactory(<element name="foo" type="xs:int"/>, Fakes.fakeSD).forRoot()
  case class Primitive1(e: Term, guard: Boolean = true) extends Terminal(e, guard) {
    def parser: Parser = Assert.notYetImplemented()
    def unparser: Unparser = Assert.notYetImplemented()
  }
  //  case class Primitive2(e: SchemaComponent, guard: Boolean = true) extends Terminal(e, guard)
  //  case class Primitive3(e: SchemaComponent, guard: Boolean = true) extends Terminal(e, guard)

  @Test def testBasicTripleSequential() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm)
    object last extends Primitive1(fakeTerm)

    lazy val triple = Prod("triple", fakeTerm, first ~ mid ~ last)

    assertFalse(triple.isEmpty)
    val str = triple.toString
    // assertFalse(str.contains("triple"))

    val exp = triple.gram
    val n = exp.name
    val s = exp.toString
    assertTrue(n.contains("SeqComp"))
    assertTrue(s.contains("first"))
    assertTrue(s.contains("mid"))
    assertTrue(s.contains("last"))
    assertTrue(s.contains(" ~ "))
  }

  @Test def testMiddleSplicedOut() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm, false)
    object last extends Primitive1(fakeTerm)

    lazy val triple = Prod("triple", fakeTerm, first ~ mid ~ last)

    assertFalse(triple.isEmpty)

    val exp = triple.gram
    // println(exp)
    assertTrue(exp.name.contains("SeqComp"))
    assertTrue(exp.toString.contains("first"))
    assertFalse(exp.toString.contains("mid")) // spliced out.
    assertTrue(exp.toString.contains("last"))
    assertTrue(exp.toString.contains(" ~ "))

  }

  @Test def testTopProdSplicedOut() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm, false)
    object last extends Primitive1(fakeTerm)

    lazy val triple = Prod("triple", fakeTerm, false, first ~ mid ~ last)

    assertTrue(triple.isEmpty)

    val exp = triple.gram
    // println(exp)
    assertFalse(exp.name.contains("SeqComp"))
    assertFalse(exp.toString.contains("first"))
    assertFalse(exp.toString.contains("mid")) // spliced out.
    assertFalse(exp.toString.contains("last"))
    assertFalse(exp.toString.contains(" ~ "))

  }

  @Test def testMultipleSpliceOuts() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm, false)
    object last extends Primitive1(fakeTerm, false)

    lazy val triple = Prod("triple", fakeTerm, first | (last ~ mid ~ first) | last)

    assertFalse(triple.isEmpty)

    val exp = triple.gram
    // println(exp)
    assertTrue(exp.name.contains("AltComp"))
    assertTrue(exp.toString.contains("first"))
    assertFalse(exp.toString.contains("mid")) // spliced out.
    assertFalse(exp.toString.contains("last")) // spliced out.
    assertTrue(exp.toString.contains(" | "))
    assertFalse(exp.toString.contains(" (")) // no interior parens. There will be around the outside though.

  }

  @Test def testPrecedence1() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm)
    object last extends Primitive1(fakeTerm)

    lazy val triple = Prod("triple", fakeTerm, first | mid ~ last)

    assertFalse(triple.isEmpty)

    val exp = triple.gram
    // println(exp)
    assertTrue(exp.name.contains("AltComp"))
    assertTrue(exp.toString.contains("first"))
    assertTrue(exp.toString.contains("mid"))
    assertTrue(exp.toString.contains("last"))
    assertTrue(exp.toString.contains(" | "))
    assertTrue(exp.toString.contains(" ~ "))
    //   assertTrue(exp.toString.contains(" | (")) // ~ binds tighter

  }

  @Test def testPrecedence2() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm)
    object last extends Primitive1(fakeTerm)

    lazy val triple = Prod("triple", fakeTerm, first ~ mid | last)

    assertFalse(triple.isEmpty)

    val exp = triple.gram
    // println(exp)
    assertTrue(exp.name.contains("AltComp"))
    assertTrue(exp.toString.contains("first"))
    assertTrue(exp.toString.contains("mid"))
    assertTrue(exp.toString.contains("last"))
    assertTrue(exp.toString.contains(" | "))
    assertTrue(exp.toString.contains(" ~ "))
    //   assertTrue(exp.toString.contains(") | ")) // ~ binds tighter

  }

  @Test def testProdsSpliceOut() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm, false)
    object last extends Primitive1(fakeTerm)

    lazy val prod1 = Prod("prod1", fakeTerm, first ~ mid | last)
    lazy val prod2 = Prod("prod2", fakeTerm, false, first ~ mid | last)
    lazy val prod3 = Prod("prod3", fakeTerm, first ~ mid | last)
    lazy val prod4 = Prod("prod4", fakeTerm, prod1 | (prod2 ~ prod3))

    assertFalse(prod4.isEmpty)

    val exp = prod4.gram
    // println(exp)
    assertTrue(exp.name.contains("AltComp"))
    // assertTrue(exp.toString.contains("prod1"))
    assertFalse(exp.toString.contains("prod2"))
    assertFalse(exp.toString.contains("prod3"))
    assertTrue(exp.toString.contains(" | "))
    assertFalse(exp.toString.contains(" ~ "))
    assertTrue(exp.toString.contains(" | "))

  }

  @Test def testUnary() {

    object first extends Primitive1(fakeTerm)
    object mid extends Primitive1(fakeTerm)
    object last extends Primitive1(fakeTerm)

    lazy val prod1 = Prod("prod1", fakeTerm, first ~ RepExactlyN(null, 1, mid) | last)

    assertFalse(prod1.isEmpty)

    val exp = prod1.gram
    // println(exp)
    assertTrue(exp.name.contains("AltComp"))
    assertTrue(exp.toString.contains("RepExactlyN"))

  }

}