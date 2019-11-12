package streams.dsl.internal.interpreters

import streams.dsl.internal.{
  EffectsInterpreter,
  MapConcatTransform,
  MapTransform,
  Pure
}
import scala.collection.immutable.Iterable

/**
  * Created by Bondarenko on Nov, 12, 2019
  *15:33.
  *Project: StreamsDSL
  */
object StreamsAPI {

  def map[A, B](f: A => B) = MapTransform(f)

  def mapConcat[A, B](f: A => Iterable[B]) = MapConcatTransform(f)

  implicit class StreamsPureRunner[F[_], A](p: Pure[F, A]) {

    def pureRun[E[_]](
      implicit interpreter: EffectsInterpreter[F, E]
    ): E[Seq[A]] =
      interpreter.runPure(p)
  }

}
