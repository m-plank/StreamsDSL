package streams.dsl

import streams.dsl.internal.Interpreter
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal.interpreters.StreamsAPI._
import scala.util.Try

trait DslExamples {
  def dsl[F[_]: Interpreter] =
    from(text("src/test/resources/numbers.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .pure()
}

object Fs2Example extends App with DslExamples {
  import streams.dsl.FS2Implicits._

  def values = dsl.pureRun.unsafeRunSync().toList
  //List(1, 1, 2, 3, 5, 8)

}

object AkkaStreamsExample extends App with DslExamples {
  import streams.dsl.AkkaImplicits._

  def values = dsl.pureRun.unsafeRunSync().toList
  //List(1, 1, 2, 3, 5, 8)

}
