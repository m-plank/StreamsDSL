package streams.dsl

import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal._
import streams.dsl.internal.interpreters.StreamsAPI._
import scala.util.Try
import cats.implicits._
import streams.dsl.internal.algebra.{
  DropWhileOp,
  FileInput,
  TextFileOutput,
  FilterOp,
  PureInput,
  SplitConcatTransform,
  TakeWhileOp,
  TextFileInput
}

/**
  * Created by Bondarenko on Nov, 13, 2019
  * 11:46.
  * Project: StreamsDSL
  */
trait DSLSamples {
  def pureDsl[F[_]: Interpreter]() = {
    from(TextFileInput("src/test/resources/numbers.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .pure()

  }

  def bytesStreamDsl[F[_]: Interpreter]() = {
    from(FileInput[Byte]("src/test/resources/numbers.txt")).pure()

  }

  def mapConcatDsl[F[_]: Interpreter] =
    from(PureInput(List("No pain no gain")))
      .through(mapConcat(_.split(" ").toList))
      .pure()

  def filterOpsDsl[F[_]: Interpreter] =
    from(PureInput(Range(0, 11).toList))
      .dropWhile(DropWhileOp(_ < 2))
      .takeWhile(TakeWhileOp(_ < 8))
      .filter(FilterOp(_ % 2 == 0))
      .pure()

  def splitConcatDsl[F[_]: Interpreter] =
    from(
      PureInput(
        List(
          "No",
          "pain",
          "no",
          "gain",
          "==",
          "Refused",
          "to",
          "give",
          "up",
          "quick",
          "=="
        )
      )
    ).splitConcat(
        SplitConcatTransform[String, String](_ == "==", (a, b) => s"$a $b".trim)
      )
      .pure()

  //

  def impureDsl[F[_]: Interpreter]() = {
    from(TextFileInput("src/test/resources/numbers.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .through(map(_.toString))
      .to(TextFileOutput("out.txt"))

  }
}
