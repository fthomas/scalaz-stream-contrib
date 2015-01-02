package eu.timepit.scalaz.stream.contrib

import scalaz.stream.Process._
import scalaz.stream.Process1
import scalaz.{==>>, Order}

/**
 * An implementation of the Lossy Counting algorithm as described in [1].
 *
 * [1]: Gurmeet Singh Manku and Rajeev Motwani. 2002. Approximate frequency
 * counts over data streams. In Proceedings of the 28th international
 * conference on Very Large Data Bases (VLDB '02). VLDB Endowment 346-357.
 *
 * @see [[http://dl.acm.org/citation.cfm?id=1287369.1287400]]
 */
object LossyCounting {

  case class ElemFreq[A](elem: A, freq: Long, maxError: Long) {
    def increment: ElemFreq[A] = copy(freq = freq + 1)
  }

  case class FreqTable[A: Order](table: A ==>> ElemFreq[A], total: Long, error: Double) {
    def insert(elem: A, maxError: Long): FreqTable[A] = {
      val newFreq = ElemFreq(elem, 1, maxError)
      copy(table.insertWith((_, existing) => existing.increment, elem, newFreq), total + 1)
    }

    def prune(bucketId: Long): FreqTable[A] =
      copy(table.filter(e => e.freq + e.maxError > bucketId))

    def frequencies(support: Double): List[ElemFreq[A]] = {
      require(support > 0.0 && support < 1.0, "support is outside of (0,1)")

      table.filter(e => e.freq >= (support - error) * total).values
    }
  }

  object FreqTable {
    def empty[A: Order](error: Double): FreqTable[A] =
      FreqTable(==>>.empty[A, ElemFreq[A]], 0, error)
  }

  def lossyCount[A: Order](error: Double): Process1[A, FreqTable[A]] = {
    require(error > 0.0 && error < 1.0, "error is outside of (0,1)")

    val width = math.ceil(1.0 / error).toLong
    def bucketId(total: Long): Long = math.ceil(total.toDouble / width).toLong

    def go(table: FreqTable[A]): Process1[A, FreqTable[A]] =
      receive1 { a =>
        val n = table.total + 1
        val b = bucketId(n)

        val pruned = if (n % width == 0) table.prune(b) else table
        val updated = pruned.insert(a, b - 1)
        emit(updated) ++ go(updated)
      }
    go(FreqTable.empty[A](error))
  }
}
