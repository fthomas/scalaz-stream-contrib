package eu.timepit.streamz.contrib

import org.scalacheck._
import org.scalacheck.Prop._
import scalaz.std.anyVal._
import scalaz.std.vector._
import scalaz.syntax.equal._
import scalaz.stream.Process._

import process1._

class Process1Spec extends Properties("process1") {
  property("defaultDistinct") = forAll { (v: Vector[Int]) =>
    emitAll(v).pipe(defaultDistinct).toIndexedSeq.toVector === v.distinct
  }
}
