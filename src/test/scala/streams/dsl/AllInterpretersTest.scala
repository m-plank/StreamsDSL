package streams.dsl

import org.scalatest.{FlatSpec, Matchers}
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal.interpreters.StreamsAPI._
import streams.dsl.internal.{Interpreter, Pure, PureInput}
import streams.dsl.{AkkaImplicits => akka, FS2Implicits => fs2}

/**
  * Created by Bondarenko on Nov, 15, 2019
  * 15:08.
  * Project: StreamsDSL
  */
class AllInterpretersTest extends FlatSpec with Matchers with DSLSamples {

  def simpleLinesDsl[F[_]: Interpreter] =
    from(PureInput(List("fs2", "akka", "streams"))).pure()

  def execWithAkka[A](dsl: Pure[AkkaImplicits.STREAM, A]): Seq[A] = {
    import AkkaImplicits._
    dsl.pureRun.unsafeRunSync()
  }

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

}
