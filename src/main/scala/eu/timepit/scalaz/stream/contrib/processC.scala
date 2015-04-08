package eu.timepit.scalaz.stream.contrib

import scalaz.Scalaz._
import scalaz.\/
import scalaz.stream.Cause.Error
import scalaz.stream.Process
import scalaz.stream.Process._

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

}
