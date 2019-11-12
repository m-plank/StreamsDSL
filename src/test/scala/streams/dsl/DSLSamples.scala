package streams.dsl

import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal._
import streams.dsl.internal.interpreters.StreamsAPI._
import scala.util.Try

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

  def impureDsl[F[_]: Interpreter]() = {
    from(TextFileInput("src/test/resources/numbers.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .to(FileOutput("out.txt"))

  }
}
