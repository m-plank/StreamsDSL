package streams.dsl

import org.scalatest.{FlatSpec, Matchers}
import streams.dsl.internal.interpreters.StreamsAPI._

/**
  * Created by Bondarenko on Nov, 13, 2019
  * 11:48.
  * Project: StreamsDSL
  */
class FS2InterpreterTest extends FlatSpec with Matchers with DSLSamples {

  import streams.dsl.FS2Implicits._

  "FS2Interpreter" should "execute pure streams dsl" in {

    pureDsl[STREAM]().pureRun.unsafeRunSync() shouldBe
      List(1, 1, 2, 3, 5, 8)
  }

  it should "execute bytes stream" in {

    bytesStreamDsl[STREAM]().pureRun.unsafeRunSync() shouldBe
      List(49, 10, 49, 10, 50, 10, 51, 10, 53, 10, 56)
  }

}
