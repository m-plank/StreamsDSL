package streams.dsl.internal.interpreters.fs2

import cats.effect.{ContextShift, IO}
import fs2.{Stream, io, text}
import streams.dsl.internal._
import java.nio.file.Paths

/**
  * Created by Bondarenko on Nov, 12, 2019
  * 21:05.
  * Project: StreamsDSL
  */
trait FS2 extends FS2Utils {
  import scala.concurrent.ExecutionContext.global

  type STREAM[A] = Stream[IO, A]

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  object Fs2Interpreter extends Interpreter[STREAM] {

    private[internal] def fromInput[A](in: Input[A]): STREAM[A] = in match {
      case PureInput(seq) => Stream.emits(seq.toSeq)
      case FileInput(path) =>
        io.file
          .readAll[IO](Paths.get(path), global, 4096)
          .asInstanceOf[STREAM[A]]
      case TextFileInput(path) =>
        io.file
          .readAll[IO](Paths.get(path), global, 4096)
          .through(text.utf8Decode)
          .through(text.lines)

    }

    private[internal] def transform[A, B](
      s: STREAM[A],
      transform: Transform[A, B]
    ): STREAM[B] = transform match {
      case MapTransform(f)       => s.map(f)
      case MapConcatTransform(f) => mapConcat(s)(f)
    }

    private[internal] def collect[A, B](s: STREAM[A],
                                        pf: PartialFunction[A, B]): STREAM[B] =
      s.collect(pf)

  }

  object fs2EffectInterpreter extends EffectsInterpreter[STREAM, IO] {
    def exec[A](fa: STREAM[A]): IO[Seq[A]] = fa.compile.toList
  }

}
