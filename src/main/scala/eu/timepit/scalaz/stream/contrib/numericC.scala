package eu.timepit.scalaz.stream.contrib

import scalaz.stream.Process1
import scalaz.stream.process1._

object numericC {

  def movingAverage[F](n: Int)(implicit F: Fractional[F]): Process1[F, F] = {
    val nf = F.fromInt(n)
    sliding[F](n).map(w => F.div(w.sum, nf))
  }

  // https://climategrog.wordpress.com/2013/05/19/triple-running-mean-filters/
  def tripleRunningMean[F](n: Int)(implicit F: Fractional[F]): Process1[F, F] = {
    val f = 1.3371
    val nd = n.toDouble
    val n2 = (nd / f).toInt
    val n3 = (nd / f / f).toInt
    movingAverage(n) |> movingAverage(n2) |> movingAverage(n3)
  }

  def prefixMeans[F](implicit F: Fractional[F]): Process1[F, F] = {
    val div = (F.div _).tupled
    prefixSums[F].zipWithIndex[F].drop(1).map(div)
  }

  def prefixProducts[N](implicit N: Numeric[N]): Process1[N, N] =
    scan(N.one)(N.times)

  def product[N](implicit N: Numeric[N]): Process1[N, N] =
    fold(N.one)(N.times)

}
