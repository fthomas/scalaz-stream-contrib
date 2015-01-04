package eu.timepit.scalaz.stream.contrib

import scalaz.stream.Process1
import scalaz.stream.process1._

object numericC {

  def movingAverage[F](n: Int)(implicit F: Fractional[F]): Process1[F, F] = {
    val nf = F.fromInt(n)
    sliding[F](n).map(w => F.div(w.sum, nf))
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
