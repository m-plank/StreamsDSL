package streams.dsl.internal.interpreters

import cats.kernel.Monoid
import streams.dsl.internal._
import scala.io.Source

/**
  * Created by Bondarenko on Nov, 11, 2019
  * 16:52.
  * Project: StreamsDSL
  */
object SimpleInterpreter extends Interpreter[Stream] {

  private[internal] def fromInput[A](in: Input[A]): Stream[A] = in match {
    case PureInput(seq) => seq.toStream
    case FileInput(path) =>
      Source
        .fromFile(path)
        .toList
        .map(_.toByte)
        .toStream
        .asInstanceOf[Stream[A]]
    case TextFileInput(path) => Source.fromFile(path).getLines().toStream
  }

  private[internal] def transform[A, B](s: Stream[A],
                                        transform: Transform[A, B]): Stream[B] =
    transform match {
      case MapTransform(f)       => s.map(f)
      case MapConcatTransform(f) => s.flatMap(f)
    }

  private[internal] def collect[A, B](s: Stream[A],
                                      pf: PartialFunction[A, B]): Stream[B] =
    s.collect(pf)

  private[internal] def splitConcat[A, B: Monoid](
    s: Stream[A],
    f: SplitConcatTransform[A, B]
  ) = {
    val m = implicitly[Monoid[B]]
    s.foldLeft(Stream.empty[B])(
      (acc, b) =>
        if (f.splitBy(b)) Stream.empty[B]
        else (f.concat(acc.headOption.getOrElse(m.empty), b)) #:: acc
    )
  }
}
