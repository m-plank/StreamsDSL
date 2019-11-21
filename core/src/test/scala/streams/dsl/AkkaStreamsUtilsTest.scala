package streams.dsl

import akka.NotUsed
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import org.scalatest.{FlatSpec, Matchers}
import streams.dsl.internal.interpreters.akka.AkkaStreamsUtils

/**
  * Created by Bondarenko on Nov, 14, 2019
  * 10:49.
  * Project: StreamsDSL
  */
class AkkaStreamsUtilsTest
    extends FlatSpec
    with Matchers
    with AkkaStreamsUtils {

  "AkkaStreamsUtils" should "read lines from file" in {
    toIO {
      readLinesFromFile("core/src/test/resources/numbers.txt").runWith(
        Sink.collection
      )
    }.map(_.toList)
      .unsafeRunSync() shouldBe (List("1", "1", "2", "3", "5", "8"))
  }

  "AkkaStreamsUtils" should "read bytes from file" in {
    toIO {
      readBytesFromFile[ByteString]("core/src/test/resources/numbers.txt")
        .map(_.toList)
        .runWith(Sink.collection)
    }.map(_.toList.flatten.toList)
      .unsafeRunSync() shouldBe List(49, 10, 49, 10, 50, 10, 51, 10, 53, 10, 56)
  }

  it should "allow 1 to many conversions" in {
    toIO {
      mapConcat[NotUsed, String, Char](
        fromSeq(List("Sucker for pain", "Take a look around"))
      ) { s =>
        s.split(" ").map(_.charAt(0)).toList
      }.runWith(Sink.collection)
    }.unsafeRunSync() shouldBe List('S', 'f', 'p', 'T', 'a', 'l', 'a')
  }

}
