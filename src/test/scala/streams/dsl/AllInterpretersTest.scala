package streams.dsl

import cats.effect.IO
import cats.scalatest.EitherMatchers
import org.scalatest.{FlatSpec, Matchers}
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal.algebra.{Pure, PureInput}
import streams.dsl.internal.interpreters.StreamsAPI._
import streams.dsl.internal.Interpreter
import streams.dsl.utils.IOUtils
import streams.dsl.{AkkaImplicits => akka, FS2Implicits => fs2}

/**
  * Created by Bondarenko on Nov, 15, 2019
  * 15:08.
  * Project: StreamsDSL
  */
class AllInterpretersTest
    extends FlatSpec
    with Matchers
    with DSLSamples
    with IOUtils
    with EitherMatchers {

  def simpleLinesDsl[F[_]: Interpreter] =
    from(PureInput(List("fs2", "akka", "streams"))).pure()

  "All interpreters" should "create stream from pure input" in {
    simpleLinesDsl(akka.int)
      .pureRun(akka.eff)
      .unsafeRunSync() shouldBe List("fs2", "akka", "streams")

    simpleLinesDsl(fs2.int)
      .pureRun(fs2.eff)
      .unsafeRunSync() shouldBe List("fs2", "akka", "streams")
  }

  it should "map concat stream" in {
    val expected = List("No", "pain", "no", "gain")

    mapConcatDsl(akka.int)
      .pureRun(akka.eff)
      .unsafeRunSync() shouldBe expected

    mapConcatDsl(fs2.int)
      .pureRun(fs2.eff)
      .unsafeRunSync() shouldBe expected

  }

  it should "split concat stream" in {
    val expected = List("No pain no gain", "Refused to give up quick")

    splitConcatDsl(akka.int)
      .pureRun(akka.eff)
      .unsafeRunSync() shouldBe expected

    splitConcatDsl(fs2.int)
      .pureRun(fs2.eff)
      .unsafeRunSync() shouldBe expected

  }

  it should "filter, drop while and take while elements in stream" in {

    filterOpsDsl(akka.int)
      .pureRun(akka.eff)
      .unsafeRunSync() shouldBe List(2, 4, 6)

    filterOpsDsl(fs2.int)
      .pureRun(fs2.eff)
      .unsafeRunSync() shouldBe List(2, 4, 6)

  }

  it should "execute effectfull stream with akka streams" in {
    val out = "out.txt"

    withFile(out) {
      impureDsl()(akka.int)
        .runWithEffects[IO](akka.eff)
        .attempt
        .unsafeRunSync() should beRight(())

      linesFromFile(out) should beRight(List("1", "1", "2", "3", "5", "8"))
    }

  }

  it should "execute effectfull stream with FS2 streams" in {
    val out = "out.txt"

    withFile(out) {
      impureDsl()(fs2.int)
        .runWithEffects[IO](fs2.eff)
        .attempt
        .unsafeRunSync() should beRight(())

      linesFromFile(out) should beRight(List("1", "1", "2", "3", "5", "8"))
    }

  }

}
