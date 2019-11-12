package streams.dsl

import streams.dsl.internal.interpreters.fs2.FS2

/**
  * Created by Bondarenko on Nov, 15, 2019
  * 10:56.
  * Project: StreamsDSL
  */
object FS2Implicits extends FS2 {

  implicit val int = Fs2Interpreter
  implicit val eff = fs2EffectInterpreter

}
