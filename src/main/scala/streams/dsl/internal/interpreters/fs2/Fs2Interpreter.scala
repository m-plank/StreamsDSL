package streams.dsl.internal.interpreters.fs2

import cats.effect.{ContextShift, IO, Resource}
import cats.kernel.Monoid
import fs2.{Stream, io, text}
import streams.dsl.internal._
import streams.dsl.internal.algebra._
import java.nio.file.Paths
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

/**
  * Created by Bondarenko on Nov, 12, 2019
  * 21:05.
  * Project: StreamsDSL
  */
trait FS2 extends FS2Utils {
  import scala.concurrent.ExecutionContext.global

  import cats.data.{Reader, Kleisli, ReaderT}

  private lazy val blockingExecutionContext =
    Resource.make(
      IO(ExecutionContext.fromExecutorService(Executors.newCachedThreadPool()))
    )(ec => IO(ec.shutdown()))

  type STREAM[A] = Stream[IO, A]

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  object Fs2Interpreter extends Interpreter[STREAM] {

    private val chunkSize = 4096

    private[internal] def fromInput[A](in: Input[A]): STREAM[A] = in match {
      case PureInput(seq) => Stream.emits(seq.toSeq)

      case FileInput(path) =>
        io.file
          .readAll[IO](Paths.get(path), global, chunkSize)
          .asInstanceOf[STREAM[A]]

      case TextFileInput(path) =>
        io.file
          .readAll[IO](Paths.get(path), global, chunkSize)
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

    private[internal] def splitConcat[A, B: Monoid](
      s: STREAM[A],
      transform: SplitConcatTransform[A, B]
    ) = {
      val m = implicitly[Monoid[B]]
      s.split(transform.splitBy).through { in =>
        in.flatMap(chunk => Stream(chunk.foldLeft(m.empty)(transform.concat)))

      }
    }

    private[internal] def collect[A, B](s: STREAM[A],
                                        pf: PartialFunction[A, B]): STREAM[B] =
      s.collect(pf)

    private[internal] def filter[A](s: STREAM[A], filterOp: FilterOps[A]) =
      filterOp match {
        case DropWhileOp(predicate) => s.dropWhile(predicate)
        case TakeWhileOp(predicate) => s.takeWhile(predicate)
        case FilterOp(predicate)    => s.filter(predicate)
      }

    private[internal] def zipWithIndex[A](s: STREAM[A]) = s.zipWithIndex
  }

  object fs2EffectInterpreter extends EffectsInterpreter[STREAM, IO] {
    //import scala.concurrent.ExecutionContext.global

    def exec[A](fa: STREAM[A]): IO[Seq[A]] = fa.compile.toList

    def execEffect[A](fa: Sink[STREAM, A]): IO[Unit] = {
      fa.out match {
        case TextFileOutput(path) =>
          Stream
            .resource(blockingExecutionContext)
            .flatMap { blockingEC =>
              fa.s.stream
                .map(_.toString)
                .intersperse("\n")
                .through(text.utf8Encode)
                .through {
                  io.file.writeAll(Paths.get(path), blockingEC)
                }

            }
            .compile
            .drain

      }
    }
  }

}
