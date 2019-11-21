package streams.dsl.internal.interpreters.fs2

import fs2.{Chunk, Pipe, Stream}
import scala.collection.immutable.Iterable

/**
  * Created by Bondarenko on Nov, 15, 2019
 12:08.
 Project: StreamsDSL
  */
trait FS2Utils {

  private def transform[F[_], A, B](f: A => Iterable[B]): Pipe[F, A, B] =
    _.flatMap(s => Stream.chunk(Chunk.seq(f(s).toVector)))

  def mapConcat[F[_], A, B](
    s: Stream[F, A]
  )(f: A => Iterable[B]): Stream[F, B] = {
    s.through(transform(f))
  }

}
