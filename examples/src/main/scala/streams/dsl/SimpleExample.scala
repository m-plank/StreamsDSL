package streams.dsl

import streams.dsl.internal.Interpreter
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal.interpreters.StreamsAPI._
import scala.util.Try

/**
  * Created by Bondarenko on Nov, 20, 2019
 23:29.
 Project: StreamsDSL
  */
object SimpleExample extends App {
  def writeToFileExpression[F[_]: Interpreter](path: String) = {
    from(text("examples/src/resources/celsius.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .through(map(_.toString))
      .to(writeToFile(path))

  }
}
