package eu.timepit.scalaz.stream.contrib

import com.nicta.rng.Rng

import scalaz.concurrent.Task
import scalaz.stream.Process
import scalaz.stream.Process._
import scalaz.{ Catchable, \/, ~> }

object rng {
  def boolean: Process[Rng, Boolean] =
    repeatEval(Rng.boolean)

  def int: Process[Rng, Int] =
    repeatEval(Rng.int)

  def long: Process[Rng, Long] =
    repeatEval(Rng.long)

  def float: Process[Rng, Float] =
    repeatEval(Rng.float)

  def double: Process[Rng, Double] =
    repeatEval(Rng.double)

  implicit class ProcessRngSyntax[O](val self: Process[Rng, O]) extends AnyVal {
    def toSource: Process[Task, O] =
      self.translate(rngToTask)
  }

  val rngToTask: Rng ~> Task = new ~>[Rng, Task] {
    def apply[A](rng: Rng[A]): Task[A] =
      Task.delay(rng.run.unsafePerformIO())
  }

  implicit val rngCatchable: Catchable[Rng] = new Catchable[Rng] {
    def fail[A](err: Throwable): Rng[A] = ???

    def attempt[A](f: Rng[A]): Rng[Throwable \/ A] = f.map(\/.right)
  }
}
