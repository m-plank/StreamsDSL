package streams.dsl.internal

import cats.kernel.Monoid
import streams.dsl.internal.algebra.{
  FilterOps,
  Input,
  Pure,
  Sink,
  SplitConcatTransform,
  Transform
}

/**
  * Created by Bondarenko on Nov, 11, 2019
  *16:49.
  *Project: StreamsDSL
  */
trait Interpreter[F[_]] {

  private[internal] def fromInput[A](in: Input[A]): F[A]

  private[internal] def transform[A, B](s: F[A], f: Transform[A, B]): F[B]

  private[internal] def splitConcat[A, B: Monoid](
    s: F[A],
    f: SplitConcatTransform[A, B]
  ): F[B]

  private[internal] def collect[A, B](s: F[A], pf: PartialFunction[A, B]): F[B]

  private[internal] def filter[A](s: F[A], filterOp: FilterOps[A]): F[A]

}

trait EffectsInterpreter[F[_], EFFECT[_]] {

  def exec[A](fa: F[A]): EFFECT[Seq[A]]

  def execEffect[A](fa: Sink[F, A]): EFFECT[Unit]

  private[internal] def runPure[A](pure: Pure[F, A]): EFFECT[Seq[A]] = {
    exec(pure.s.stream)
  }
}
