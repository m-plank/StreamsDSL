
[![xxxx](https://travis-ci.com/m-plank/StreamsDSL.svg)](https://travis-ci.com/m-plank/StreamsDSL)
[![codecov](https://codecov.io/gh/m-plank/StreamsDSL/branch/master/graph/badge.svg)](https://codecov.io/gh/m-plank/StreamsDSL)

# StreamsDSL

The main goal of the project is to provide abstract DSL for working with streams libraries on high level. 
So a developer can create simple and inpdependent pieces of code and switch quickly between streams implementations.
Currently the project supports [FS2](https://github.com/functional-streams-for-scala/fs2) and [Akka Streams](https://doc.akka.io/docs/akka/current/stream/index.html).

# Work in progress 

Currently the project is in active development in experimental stage.    


## Quick example
```scala
import streams.dsl.internal.StreamIOAPI.from
import streams.dsl.internal.algebra.TextFileInput
import streams.dsl.internal.interpreters.StreamsAPI._
import streams.dsl.internal.{ Interpreter, MapTransform }
import scala.util.Try

trait DslExamples {
  def dsl[F[_]: Interpreter] =
    from(TextFileInput("src/test/resources/numbers.txt"))
      .through(map(str => Try(str.toInt).toEither))
      .collect { case Right(v) => v }
      .pure()
}

object Fs2Example extends App with DslExamples {
  import streams.dsl.FS2Implicits._

  def values = dsl.pureRun.unsafeRunSync().toList
  //List(1, 1, 2, 3, 5, 8)

}

object AkkaStreamsExample extends App with DslExamples {
  import streams.dsl.AkkaImplicits._

  def values = dsl.pureRun.unsafeRunSync().toList
  //List(1, 1, 2, 3, 5, 8)

}

```

See also [Celsius example](https://github.com/m-plank/StreamsDSL/blob/master/examples/src/main/scala/streams/dsl/CelsiusFs2Example.scala)


