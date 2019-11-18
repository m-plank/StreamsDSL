package streams.dsl.internal.algebra

import cats.kernel.Monoid
import streams.dsl.internal.Interpreter

/**
  * Created by Bondarenko on Nov, 11, 2019
  * 16:47.
  * Project: StreamsDSL
  */
sealed trait AbsStream[F[_], A]

sealed abstract class StreamQ[F[_]: Interpreter, A] extends AbsStream[F, A] {

  private[internal] def int = implicitly[Interpreter[F]]

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

case class FromInput[F[_]: Interpreter, A](private val in: Input[A])
    extends StreamQ[F, A] {
  private[internal] def stream: F[A] = int.fromInput(in)
}

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
  private[internal] def stream: F[B] = int.splitConcat(s.stream, splitConcat)
}

case class Collect[F[_]: Interpreter, A, B](
  private val s: StreamQ[F, A],
  private val pf: PartialFunction[A, B]
) extends StreamQ[F, B] {
  private[internal] def stream: F[B] = int.collect(s.stream, pf)
}

case class Filter[F[_]: Interpreter, A](private val s: StreamQ[F, A],
                                        private val filterOp: FilterOps[A])
    extends StreamQ[F, A] {
  private[internal] def stream: F[A] = int.filter(s.stream, filterOp)
}

case class Sink[F[_]: Interpreter, A](private[internal] val s: StreamQ[F, A],
                                      private[internal] val out: Output[A])
    extends AbsStream[F, A]

case class Pure[F[_]: Interpreter, A](private[internal] val s: StreamQ[F, A])
    extends AbsStream[F, A]
