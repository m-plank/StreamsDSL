package streams.dsl.internal

/**
  * Created by Bondarenko on Nov, 11, 2019
 16:50.
 Project: StreamsDSL
  */
object StreamIOAPI {
  def from[F[_]: Interpreter, A](in: Input[A]): StreamQ[F, A] =
    FromInput[F, A](in)
}
