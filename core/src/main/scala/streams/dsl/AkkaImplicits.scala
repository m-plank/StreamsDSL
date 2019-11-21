package streams.dsl

import streams.dsl.internal.interpreters.akka.AkkaStreams

/**
  * Created by Bondarenko on Nov, 15, 2019
  *10:59.
  *Project: StreamsDSL
  */
object AkkaImplicits extends AkkaStreams {

  implicit val int = akkaStreamsInterpreter
  implicit val eff = akkaEffectInterpreter
}
