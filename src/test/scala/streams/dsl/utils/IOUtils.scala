package streams.dsl.utils

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by Bondarenko on Nov, 19, 2019
  * 21:45.
  * Project: StreamsDSL
  */
trait IOUtils {

  def deleteFile(path: String) =
    Try { Files.deleteIfExists(Paths.get(path)) }.toEither

  def linesFromFile(path: String) =
    Try(Source.fromFile(path).getLines().toList).toEither

  def withFile[A](path: String)(f: => A) = {
    deleteFile(path)
    val result = Try(f)
    deleteFile(path)
    result match {
      case Failure(exception) => throw exception
      case Success(value)     => value
    }
  }

}
