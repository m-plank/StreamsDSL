package streams.dsl

import akka.util.ByteString
import org.scalatest.{FlatSpec, Matchers}
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal.algebra.FileInput
import streams.dsl.internal.interpreters.StreamsAPI._

/**
  * Created by Bondarenko on Nov, 14, 2019
  * 10:43.
  * Project: StreamsDSL
  */
class AkkaStreamsInterpreterTest
    extends FlatSpec
    with Matchers
    with DSLSamples {

  import streams.dsl.AkkaImplicits._

  "AkkaStreamsInterpreter" should "execute pure streams dsl" in {
    pureDsl[STREAM]().pureRun.unsafeRunSync() shouldBe (List(1, 1, 2, 3, 5, 8))
  }

  "AkkaStreamsInterpreter" should "read bytes from input file" in {
    val dsl = from(FileInput[ByteString]("core/src/test/resources/numbers.txt"))
      .pure()

    dsl.pureRun.map(_.flatten).unsafeRunSync() shouldBe
      List(49, 10, 49, 10, 50, 10, 51, 10, 53, 10, 56)

  }

  ignore should "read bytes from input file with incorrect type param" in {
    val dsl = from(FileInput[Byte]("core/src/test/resources/numbers.txt"))
      .pure()

    dsl.pureRun.unsafeRunSync().toList shouldBe
      List(49, 10, 49, 10, 50, 10, 51, 10, 53, 10, 56)

  }

}
