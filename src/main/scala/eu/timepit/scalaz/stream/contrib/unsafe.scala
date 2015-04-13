package eu.timepit.scalaz.stream.contrib

import scalaz.{ Catchable, Functor, \/ }

object unsafe {
  implicit def fakeCatchable[F[_]](implicit F: Functor[F]): Catchable[F] =
    new Catchable[F] {
      def attempt[A](f: F[A]): F[Throwable \/ A] =
        F.map(f)(\/.right)

      def fail[A](err: Throwable): F[A] =
        throw new NotImplementedError("fakeCatchable does not implement fail")
    }
}
