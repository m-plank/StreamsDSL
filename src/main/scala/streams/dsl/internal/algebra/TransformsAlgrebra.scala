package streams.dsl.internal.algebra

import cats.kernel.Monoid
import scala.collection.immutable.Iterable

/**
  * Created by Bondarenko on Nov, 18, 2019
  * 19:48.
  * Project: StreamsDSL
  */
sealed trait Input[A]
case class PureInput[A](seq: Iterable[A]) extends Input[A]
case class FileInput[A](path: String) extends Input[A]
case class TextFileInput(path: String) extends Input[String]

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

sealed trait Output[A]
case class FileOutput[A](private val path: String) extends Output[A]
