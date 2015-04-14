package eu.timepit.scalaz.stream.contrib

import scalaz.Scalaz._
import scalaz.stream.Cause.Error
import scalaz.stream.Process._
import scalaz.stream.{ Channel, Process, Writer }
import scalaz.{ Bind, \/ }

object processC {

  implicit class ProcessIdSyntax[O](val self: Process[Id, O]) extends AnyVal {
    def toList: List[O] =
      toStream.toList

    def toStream: Stream[O] = {
      def go(p: Process[Id, O]): Stream[O] =
        p.step match {
          case Step(Emit(os), next) => os.toStream #::: go(next.continue)
          case Step(Await(req, rcv), next) => go(rcv(\/.right(req)).run +: next)
          case Halt(Error(rsn)) => throw rsn
          case Halt(_) => Stream.empty
        }
      go(self)
    }
  }

  implicit class MyChannelSyntax[F[_], I, O](val self: Channel[F, I, O]) extends AnyVal {
    def andThen[O2](f: O => F[O2])(implicit F: Bind[F]): Channel[F, I, O2] =
      self.map(g => g.andThen(_.flatMap(f)))

    def compose[I0](f: I0 => F[I])(implicit F: Bind[F]): Channel[F, I0, O] =
      self.map(g => f.andThen(_.flatMap(g)))
  }

  // https://github.com/scalaz/scalaz-stream/pull/358
  implicit class MyWriterSyntax[F[_], W, O](val self: Writer[F, W, O]) extends AnyVal {
    def ignoreO: Writer[F, W, Nothing] =
      self.flatMapO(_ => halt)

    def ignoreW: Writer[F, Nothing, O] =
      self.flatMapW(_ => halt)
  }

}
