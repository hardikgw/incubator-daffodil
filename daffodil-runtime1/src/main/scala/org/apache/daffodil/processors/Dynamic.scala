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

package org.apache.daffodil.processors

import org.apache.daffodil.util.Maybe
import org.apache.daffodil.util.Maybe._

/**
 * Takes a compiled expression and a conversion, and if the expression is a constant,
 * runs the conversion once saving the converted result. This saves work if this
 * happens at compilation time, not runtime.
 *
 * If the expression is dynamic, then
 * it runs the expression then the conversion once, and saves the result.
 *
 * Note that the "only once" aspect of this isn't terribly important, as one would not
 * expect parse/unparse operations to go back and ask for these expressions repeatedly. They
 * should be keeping it in a local val.
 */
trait Dynamic {

  // TODO: Performance - is this cache necessary? Seems to only be used in text number format
  // situations. Why would any of these be referenced more than once in parse or unparse of a simple
  // text number type element?  (Can't these just be lazy val on the infoset element then?)

  // TODO: Performance - we should consider avoiding using an Either object here since they are
  // allocated every time this is called. Getting allocation down to where meaningful and necessary
  // objects are allocated is important, and little tuples and Some(..) and Right/Left objects
  // are not created makes a great deal of performance difference, albeit at the cost of making the
  // code slighly more clumsy.

  // We can use an AnyRef, and use either a value or a compiled expression, and methods that embed
  // the cast, answer isExpression or isValue, etc.
  // i.e, Create a value class named EitherOr that plays the same tricks as Maybe. Probably has to be
  // special for CompiledExpression because we have to have a non-generic thing we can test for
  // isInstanceOf that isn't erased. (Should build this right into compiled expression - i.e.,
  // have evaluate method and getCache() method which returns object which has evaluate() also, but caches))
  //
  // However, we still have the box/unbox problem if the values of these are numbers. May not matter
  // as these caches are not hit a lot.
  //

  //
  // TODO: Complexity - seems excessively complex
  //
  // Dynamic is a mixin. It's used only in two places - escape schemes (because escChar and escescChar)
  // and text number formats.
  //
  // The type A appears only as String, List[Char], Character. Generic [A] type may be overkill. Or
  // generic should just call a common thing that is wired for String, List[Char] or Character, and has the
  // converter built into it. So no function object needs to be passed, etc.
  //
  // There shouldn't be a need for NumFormat static and dynamic variants. That's a redundant distinction
  // that is being hidden at this level in theory. Yet there is the static/dynamic distinction being made
  // at that level also.
  //

  // We can use an AnyRef, and use either a value or a compiled expression, and methods that embed
  // the cast, answer isExpression or isValue, etc.
  // i.e, Create a value class named EitherOr that plays the same tricks as Maybe. Probably has to be
  // special for CompiledExpression because we have to have a non-generic thing we can test for
  // isInstanceOf that isn't erased. (Should build this right into compiled expression - i.e.,
  // have evaluate method and getCache() method which returns object which has evaluate() also, but caches))
  //
  // However, we still have the box/unbox problem if the values of these are numbers. May not matter
  // as these caches are not hit a lot.
  //
  // Dynamic is a mixin. It's used only in two places - escape schemes (because escChar and escescChar)
  // and text number formats.
  //
  // The type A appears only as String, List[Char], Character. Generic [A] type may be overkill. Or
  // generic should just call a common thing that is wired for String, List[Char] or Character, and has the
  // converter built into it.
  //
  // There shouldn't be a need for NumFormat static and dynamic variants. That's a redundant distinction
  // that is being hidden at this level in theory.
  //
  type CachedDynamic[A <: AnyRef, B <: AnyRef] = Either[Evaluatable[A], B]

  // Returns an Either, with Right being the value of the constant, and the
  // Left being the a non-constant compiled expression. The conv variable is
  // used to convert the constant value to a more usable form, and perform and
  // SDE checks. This should be called during initialization/compile time. Not
  // during runtime.
  def cacheConstantExpression[A <: AnyRef, B <: AnyRef](e: Evaluatable[A])(conv: (A) => B): CachedDynamic[A, B] = {
    if (e.isConstant) {
      val v: A = e.maybeConstant.get
      Right(conv(v))
    } else {
      Left(e)
    }
  }

  // Note: These method names used to be just overloads without the "Maybe" suffix.
  // We don't really need them to be overloads, and some permutation of the Maybe[T] class
  // with lots of inlining resulted in errors here because a Maybe[T] is an AnyVal aka
  // value class. At compile time Maybe[Foo] and just Foo aren't distinguishable to resolve
  // the overloading. So keep it simple, and just don't overload the names.
  def cacheConstantExpressionMaybe[A <: AnyRef, B <: AnyRef](oe: Maybe[Evaluatable[A]])(conv: (A) => B): Maybe[CachedDynamic[A, B]] = {
    //oe.map { e => cacheConstantExpression[A](e)(conv) }
    if (oe.isDefined) One(cacheConstantExpression[A, B](oe.get)(conv))
    else Nope
  }

  def cacheConstantExpression[A <: AnyRef, B <: AnyRef](listOfE: List[Evaluatable[A]])(conv: (A) => B): List[CachedDynamic[A, B]] = {
    listOfE.map { e => cacheConstantExpression[A, B](e)(conv) }
  }

  // For any expression that couldn't be evaluated in cacheConstantExpression,
  // this evaluates that. This is used to evaluate only runtime expressions.
  // This also carries along PState that is modified during expression
  // evaluation.
  def evalWithConversion[A <: AnyRef, B <: AnyRef](s: ParseOrUnparseState, e: CachedDynamic[A, B])(conv: (ParseOrUnparseState, A) => B): B = {
    e match {
      case Right(r) => r
      case Left(l) => {
        val a: A = l.evaluate(s)
        if (s.processorStatus ne Success) {
          // evaluation failed
          // we can't continue this code path
          // have to throw out of here
          throw s.processorStatus.asInstanceOf[Failure].cause
        }
        val b: B = conv(s, a)
        b
      }
    }
  }

  def evalWithConversionMaybe[A <: AnyRef, B <: AnyRef](s: ParseOrUnparseState, oe: Maybe[CachedDynamic[A, B]])(conv: (ParseOrUnparseState, A) => B): Maybe[B] = {
    if (oe.isDefined) {
      val b: B = evalWithConversion[A, B](s, oe.get)(conv)
      One(b)
    } else Nope
  }

  def evalWithConversion[A <: AnyRef, B <: AnyRef](s: ParseOrUnparseState, oe: List[CachedDynamic[A, B]])(conv: (ParseOrUnparseState, A) => B): List[B] = {
    val state = s
    val listE = oe.map(e => {
      val exp: B = evalWithConversion[A, B](state, e)(conv)
      exp
    })
    listE
  }

  // With an property that can potentially be compiled, this returns an Option,
  // which is either Some(s) if the value of the property is static, or None
  // otherwise

  def getStatic[A <: AnyRef, B <: AnyRef](e: CachedDynamic[A, B]): Maybe[B] = {
    e match {
      case Left(l) => Nope
      case Right(r) => One(r)
    }
  }

  def getStaticMaybe[A <: AnyRef, B <: AnyRef](oe: Maybe[CachedDynamic[A, B]]): Maybe[B] = {
    if (oe.isDefined) getStatic(oe.get)
    else Nope
  }
}
