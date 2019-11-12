package streams.dsl.internal.interpreters.akka

import akka.stream.scaladsl.{Sink, Source}
import cats.effect._
import streams.dsl.internal._

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

    private[internal] def collect[A, B](s: STREAM[A],
                                        pf: PartialFunction[A, B]) =
      s.collect(pf)
  }

  object akkaEffectInterpreter extends EffectsInterpreter[STREAM, IO] {
    def exec[A](fa: STREAM[A]): IO[Seq[A]] = {
      toIO(fa.runWith(Sink.collection))
    }
  }

}
