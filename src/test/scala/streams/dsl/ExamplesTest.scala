package streams.dsl

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Bondarenko on Nov, 15, 2019
  * 21:44.
  * Project: StreamsDSL
  */
class ExamplesTest extends FlatSpec with Matchers {

  it should "interpret examples dsl" in {
    AkkaStreamsExample.values shouldBe List(1, 1, 2, 3, 5, 8)
    Fs2Example.values shouldBe List(1, 1, 2, 3, 5, 8)
  }
}
