package eu.timepit.scalaz.stream.contrib

import scalaz.\/._
import scalaz.stream.Process._
import scalaz.stream.{ Process, Process1 }
import scalaz.stream.process1._
import scalaz.syntax.equal._
import scalaz.{ Equal, ISet, Order, \/ }

object process1C {

  def defaultDistinct[A]: Process1[A, A] =
    defaultDistinctBy(identity)

  def defaultDistinctBy[A, B](f: A => B): Process1[A, A] = {
    def go(seen: Set[B]): Process1[A, A] =
      receive1 { a =>
        val b = f(a)
        if (seen.contains(b)) go(seen)
        else emit(a) ++ go(seen + b)
      }
    go(Set.empty)
  }

  def distinct[A: Order]: Process1[A, A] =
    distinctBy(identity)

  def distinctBy[A, B: Order](f: A => B): Process1[A, A] = {
    def go(seen: ISet[B]): Process1[A, A] =
      receive1 { a =>
        val b = f(a)
        if (seen.contains(b)) go(seen)
        else emit(a) ++ go(seen.insert(b))
      }
    go(ISet.empty)
  }

  def genericDrop[A, I](n: I)(implicit I: Integral[I]): Process1[A, A] = {
    import I._
    if (n <= I.zero) id
    else skip ++ genericDrop(n - I.one)
  }

  def genericTake[A, I](n: I)(implicit I: Integral[I]): Process1[A, A] = {
    import I._
    if (n <= I.zero) halt
    else await1[A] ++ genericTake(n - I.one)
  }

  def insert[A: Order](a: A): Process1[A, A] =
    insertBy(Order[A].lessThanOrEqual)(a)

  def insertBy[A](f: (A, A) => Boolean)(a: A): Process1[A, A] =
    receive1Or[A, A](emit(a)) { a2 =>
      if (f(a, a2)) Process(a, a2) ++ id
      else emit(a2) ++ insertBy(f)(a)
    }

  def nub[A: Equal]: Process1[A, A] =
    nubBy(_ === _)

  def nubBy[A](f: (A, A) => Boolean): Process1[A, A] = {
    def go(seen: List[A]): Process1[A, A] =
      receive1 { a =>
        if (seen.exists(f(a, _))) go(seen)
        else emit(a) ++ go(a :: seen)
      }
    go(List.empty)
  }

  def partition[A](p: A => Boolean): Process1[A, A \/ A] =
    lift(a => if (p(a)) right(a) else left(a))

}
