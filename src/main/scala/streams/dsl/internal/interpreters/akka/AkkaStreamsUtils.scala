package streams.dsl.internal.interpreters.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, Framing, StreamConverters, _}
import akka.util.ByteString
import cats.effect._
import java.io.FileInputStream
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

/**
  * Created by Bondarenko on Nov, 14, 2019
 10:48.
 Project: StreamsDSL
  */
trait AkkaStreamsUtils {

  import scala.collection.immutable.Iterable

  implicit val system = ActorSystem("QuickStart")
  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  def lineDelimiter: Flow[ByteString, ByteString, NotUsed] =
    Framing.delimiter(ByteString("\n"), Int.MaxValue, allowTruncation = true)

  def readBytesFromFile[A](path: String): Source[A, Future[IOResult]] =
    StreamConverters
      .fromInputStream(() => new FileInputStream(path))
      .asInstanceOf[Source[A, Future[IOResult]]] //todo: can yield in runtime errors!!!

  def readLinesFromFile(path: String) = {
    StreamConverters
      .fromInputStream(() => new FileInputStream(path))
      .via(lineDelimiter)
      .map(_.utf8String)
  }

  def fromSeq[A](s: Iterable[A]) = Source(s)

  def mapConcat[F, A, B](source: Source[A, F])(f: A => Iterable[B]) =
    source.mapConcat[B](f)

  def toIO[A](f: => Future[A]): IO[A] = IO.fromFuture(IO(f))

}
