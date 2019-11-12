package streams.dsl

import cats.effect.IO
import org.scalatest.{FlatSpec, Matchers}
import streams.dsl.internal.interpreters.SimpleInterpreter
import streams.dsl.internal.interpreters.StreamsAPI._
import streams.dsl.internal.{EffectsInterpreter, Interpreter}

/**
  * Created by Bondarenko on Nov, 12, 2019
  * 16:45.
  * Project: StreamsDSL
  */
class SimpleInterpreterTest extends FlatSpec with Matchers with DSLSamples {

  implicit val interpreter: Interpreter[Stream] = SimpleInterpreter
  implicit val eff = new EffectsInterpreter[Stream, IO] {
    def exec[A](fa: Stream[A]): IO[Seq[A]] = IO { fa }
  }

  "SimpleInterpreter" should "execute pure streams dsl" in {

    pureDsl[Stream]().pureRun.unsafeRunSync() shouldBe
      Stream(1, 1, 2, 3, 5, 8)
  }

  it should "map concat stream" in {

    mapConcatDsl[Stream].pureRun.unsafeRunSync() shouldBe
      Stream("No", "pain", "no", "gain")
  }
}
