package streams.dsl

import cats.implicits._
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal._
import streams.dsl.internal.algebra.{SplitConcatTransform, TextFileOutput}
import streams.dsl.internal.interpreters.StreamsAPI._
import scala.util.Try

/**
  * Created by Bondarenko on Nov, 13, 2019
  * 11:46.
  * Project: StreamsDSL
  */
trait DSLSamples {
  def pureDsl[F[_]: Interpreter]() = {
    from(text("src/test/resources/numbers.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .pure()

  }

  def bytesStreamDsl[F[_]: Interpreter]() = {
    from(bytes("src/test/resources/numbers.txt")).pure()

  }

  def mapConcatDsl[F[_]: Interpreter] =
    from(pure(List("No pain no gain")))
      .through(mapConcat(_.split(" ").toList))
      .pure()

  def filterOpsDsl[F[_]: Interpreter] =
    from(pure(Range(0, 11).toList))
      .dropWhile(_ < 2)
      .takeWhile(_ < 8)
      .filter(_ % 2 == 0)
      .pure()

  def splitConcatDsl[F[_]: Interpreter] =
    from(
      pure(
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

  def zipWithIndexDsl[F[_]: Interpreter] =
    from(pure(List(1, 1, 2, 3, 5)))
      .zipWithIndex()
      .pure()

  def writeToFileExpression[F[_]: Interpreter](path: String) = {
    from(text("src/test/resources/numbers.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .through(map(_.toString))
      .to(writeToFile(path))

  }
}
