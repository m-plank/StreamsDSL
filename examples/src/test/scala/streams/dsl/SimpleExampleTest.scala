package streams.dsl

import cats.scalatest.EitherMatchers
import org.scalatest.{FlatSpec, Matchers}
import cats.implicits._
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal._
import streams.dsl.internal.algebra.{SplitConcatTransform, TextFileOutput}
import streams.dsl.internal.interpreters.StreamsAPI._
import scala.util.Try

/**
  * Created by Bondarenko on Nov, 21, 2019
  *11:32.
  *Project: root
  */
class SimpleExampleTest extends FlatSpec with Matchers with EitherMatchers {

  "SimpleExample" should "convert Fahrenheit to Celsius" in {}

}
