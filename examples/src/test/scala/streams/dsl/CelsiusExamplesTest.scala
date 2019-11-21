package streams.dsl

import cats.scalatest.EitherMatchers
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Bondarenko on Nov, 21, 2019
  *11:32.
  *Project: root
  */
class CelsiusExamplesTest
    extends FlatSpec
    with Matchers
    with EitherMatchers
    with IOUtils {

  it should "convert Fahrenheit to Celsius with FS2" in {
    val path = "celsius-fs2.txt"
    withFile(path) {
      CelsiusFs2Example.run.attempt.unsafeRunSync() should beRight(())
      linesFromFile(path).map(_.head) should beRight("-7.777777777777779")
    }

  }

  it should "convert Fahrenheit to Celsius with akka" in {
    val path = "celsius-akka.txt"
    withFile(path) {
      CelsiusAkkaExample.run.attempt.unsafeRunSync() should beRight(())
      Thread.sleep(3000)
      linesFromFile(path).map(_.head) should beRight("-7.777777777777779")
    }

  }

}
