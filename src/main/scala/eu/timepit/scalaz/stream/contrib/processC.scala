package eu.timepit.scalaz.stream.contrib

import scalaz.Scalaz._
import scalaz.stream.Cause.Error
import scalaz.stream.Process
import scalaz.stream.Process._
import scalaz.{ Bind, \/ }

object processC {

  implicit class ProcessIdSyntax[O](val self: Process[Id, O]) extends AnyVal {
    def toList: List[O] =
      toStream.toList

    def toStream: Stream[O] = {
      def go(p: Process[Id, O]): Stream[O] =
        p.step match {
          case s: Step[Id, O] =>
            s.head match {
              case Emit(os) =>
                os.toStream #::: go(s.next.continue)
              case Await(req, rcv) =>
                Bind[Id].bind(req)(o => go(rcv(\/.right(o)).run +: s.next))
            }
          case Halt(Error(rsn)) => throw rsn
          case Halt(_) => Stream.empty
        }
      go(self)
    }
  }

}
