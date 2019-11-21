package streams.dsl

import cats.effect.IO
import streams.dsl.internal.Interpreter
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal.interpreters.StreamsAPI._

/**
  * Created by Bondarenko on Nov, 20, 2019
 23:29.
 Project: StreamsDSL
  */
object CelsiusFs2Example extends App with Examples {
  import streams.dsl.FS2Implicits._

  def run: IO[Unit] =
    convertToCelsius("examples/src/main/resources/fahrenheit.txt").runWithEffects

  //run.unsafeRunSync()

}

object CelsiusAkkaExample extends App with Examples {
  import streams.dsl.AkkaImplicits._

  def run: IO[Unit] =
    convertToCelsius("examples/src/main/resources/fahrenheit.txt").runWithEffects

  //run.unsafeRunSync()

}

trait Examples {
  def convertToCelsius[F[_]: Interpreter](path: String) = {
    from(text(path))
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .through(map(line => fahrenheitToCelsius(line.toDouble).toString))
      .to(writeToFile("celsius.txt"))

  }

  private def fahrenheitToCelsius(f: Double): Double =
    (f - 32.0) * (5.0 / 9.0)
}
