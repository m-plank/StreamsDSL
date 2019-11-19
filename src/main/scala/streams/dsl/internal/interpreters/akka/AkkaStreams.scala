package streams.dsl.internal.interpreters.akka

import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import cats.effect._
import cats.kernel.Monoid
import streams.dsl.internal._
import streams.dsl.internal.algebra.{
  DropWhileOp,
  FileInput,
  FilterOp,
  FilterOps,
  Input,
  MapConcatTransform,
  MapTransform,
  PureInput,
  SplitConcatTransform,
  TakeWhileOp,
  TextFileInput,
  TextFileOutput,
  Transform
}
import java.nio.file.Paths

/**
  * Created by Bondarenko on Nov, 14, 2019
  * 10:21.
  * Project: StreamsDSL
  */
trait AkkaStreams extends AkkaStreamsUtils {

  type STREAM[A] = Source[A, _]

  object akkaStreamsInterpreter extends Interpreter[STREAM] {

    private[internal] def fromInput[A](in: Input[A]): STREAM[A] = in match {
      case PureInput(seq)      => Source(seq)
      case FileInput(path)     => readBytesFromFile[A](path)
      case TextFileInput(path) => readLinesFromFile(path)
    }

    private[internal] def transform[A, B](s: STREAM[A],
                                          transform: Transform[A, B]) =
      transform match {
        case MapTransform(f)       => s.map(f)
        case MapConcatTransform(f) => s.mapConcat(f)
      }

    private[internal] def splitConcat[A, B: Monoid](
      s: STREAM[A],
      f: SplitConcatTransform[A, B]
    ) = {
      val m = implicitly[Monoid[B]]
      s.splitAfter(f.splitBy)
        .fold(m.empty)((acc, b) => if (f.splitBy(b)) acc else f.concat(acc, b))
        .concatSubstreams
    }

    private[internal] def collect[A, B](s: STREAM[A],
                                        pf: PartialFunction[A, B]) =
      s.collect(pf)

    private[internal] def filter[A](s: STREAM[A], filterOp: FilterOps[A]) =
      filterOp match {
        case DropWhileOp(predicate) => s.dropWhile(predicate)
        case TakeWhileOp(predicate) => s.takeWhile(predicate)
        case FilterOp(predicate)    => s.filter(predicate)
      }
  }

  object akkaEffectInterpreter extends EffectsInterpreter[STREAM, IO] {
    def exec[A](fa: STREAM[A]): IO[Seq[A]] = {
      toIO(fa.runWith(Sink.collection))
    }

    def execEffect[A](fa: algebra.Sink[STREAM, A]): IO[Unit] = fa.out match {
      case TextFileOutput(path) =>
        IO.delay {
          fa.s.stream
            .map(s => ByteString(s"$s\n"))
            .to(FileIO.toPath(Paths.get(path)))
            .run()
        }
    }
  }

}
