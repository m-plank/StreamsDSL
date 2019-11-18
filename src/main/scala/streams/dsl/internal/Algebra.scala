package streams.dsl.internal

import cats.kernel.Monoid
import scala.collection.immutable.Iterable

/**
  * Created by Bondarenko on Nov, 11, 2019
  * 16:47.
  * Project: StreamsDSL
  */
sealed trait AbsStream[F[_], A]

sealed abstract class StreamQ[F[_]: Interpreter, A] extends AbsStream[F, A] {

  private[internal] def stream: F[A]

  def through[B](f: Transform[A, B]): StreamQ[F, B] = Map[F, A, B](this, f)

  def collect[B](pf: PartialFunction[A, B]): StreamQ[F, B] =
    Collect(this, pf)

  def splitConcat[B: Monoid](f: SplitConcatTransform[A, B]): StreamQ[F, B] =
    SplitConcat(this, f)

  def dropWhile(op: DropWhileOp[A]): StreamQ[F, A] = Filter(this, op)

  def takeWhile(op: TakeWhileOp[A]): StreamQ[F, A] = Filter(this, op)

  def filter(op: FilterOp[A]): StreamQ[F, A] = Filter(this, op)

  def to(out: Output[A]): Sink[F, A] = Sink(this, out)

  def pure(): Pure[F, A] = Pure[F, A](this)

}

sealed trait Input[A]
case class PureInput[A](seq: Iterable[A]) extends Input[A]
case class FileInput[A](path: String) extends Input[A]
case class TextFileInput(path: String) extends Input[String]

case class FromInput[F[_]: Interpreter, A](private val in: Input[A])
    extends StreamQ[F, A] {
  private[internal] def stream: F[A] =
    implicitly[Interpreter[F]].fromInput(in)
}

sealed trait Transform[A, B]
sealed abstract class FilterOps[A](predicate: A => Boolean)
case class MapTransform[A, B](f: A => B) extends Transform[A, B]
case class MapConcatTransform[A, B](f: A => Iterable[B]) extends Transform[A, B]

case class DropWhileOp[A](predicate: A => Boolean)
    extends FilterOps[A](predicate)
case class TakeWhileOp[A](predicate: A => Boolean)
    extends FilterOps[A](predicate)
case class FilterOp[A](predicate: A => Boolean) extends FilterOps[A](predicate)

case class SplitConcatTransform[A, B: Monoid](splitBy: A => Boolean,
                                              concat: (B, A) => B)

/////////STREAM CONVERSIONS/////////////////////////////////////////////
case class Map[F[_]: Interpreter, A, B](private val s: StreamQ[F, A],
                                        private val transform: Transform[A, B])
    extends StreamQ[F, B] {
  private[internal] def stream: F[B] =
    implicitly[Interpreter[F]].transform(s.stream, transform)
}

case class SplitConcat[F[_]: Interpreter, A, B: Monoid](
  private val s: StreamQ[F, A],
  private val splitConcat: SplitConcatTransform[A, B]
) extends StreamQ[F, B] {
  private[internal] def stream: F[B] =
    implicitly[Interpreter[F]].splitConcat(s.stream, splitConcat)
}

case class Collect[F[_]: Interpreter, A, B](
  private val s: StreamQ[F, A],
  private val pf: PartialFunction[A, B]
) extends StreamQ[F, B] {
  private[internal] def stream: F[B] =
    implicitly[Interpreter[F]].collect(s.stream, pf)
}

case class Filter[F[_]: Interpreter, A](private val s: StreamQ[F, A],
                                        private val filterOp: FilterOps[A])
    extends StreamQ[F, A] {
  private[internal] def stream: F[A] =
    implicitly[Interpreter[F]].filter(s.stream, filterOp)
}

case class Sink[F[_]: Interpreter, A](private[internal] val s: StreamQ[F, A],
                                      private[internal] val out: Output[A])
    extends AbsStream[F, A]

case class Pure[F[_]: Interpreter, A](private[internal] val s: StreamQ[F, A])
    extends AbsStream[F, A]

sealed trait Output[A]
case class FileOutput[A](private val path: String) extends Output[A]
