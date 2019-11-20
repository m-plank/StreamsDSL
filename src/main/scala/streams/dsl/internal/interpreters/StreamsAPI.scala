package streams.dsl.internal.interpreters

import streams.dsl.internal.EffectsInterpreter
import streams.dsl.internal.algebra._
import scala.collection.immutable.Iterable

/**
  * Created by Bondarenko on Nov, 12, 2019
  *15:33.
  *Project: StreamsDSL
  */
object StreamsAPI {

  def text(path: String) = TextFileInput(path)

  def bytes(path: String) = FileInput[Byte](path)

  def pure[A](seq: Iterable[A]) = PureInput[A](seq)

  def map[A, B](f: A => B) = MapTransform(f)

  def writeToFile(path: String) = TextFileOutput(path)

  def mapConcat[A, B](f: A => Iterable[B]) = MapConcatTransform(f)

  implicit class StreamsPureRunner[F[_], A](p: Pure[F, A]) {

    def pureRun[E[_]](
      implicit interpreter: EffectsInterpreter[F, E]
    ): E[Seq[A]] =
      interpreter.runPure(p)
  }

  implicit class StreamsEffectRunner[F[_], A](eff: Sink[F, A]) {
    def runWithEffects[E[_]](
      implicit interpreter: EffectsInterpreter[F, E]
    ): E[Unit] = {
      interpreter.execEffect(eff)
    }
  }

}
