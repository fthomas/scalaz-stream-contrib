package eu.timepit.scalaz.stream.contrib

import scalaz.stream.Process1
import scalaz.stream.process1._

object numericC {

  def prefixMeans[F](implicit F: Fractional[F]): Process1[F, F] =
    prefixSums[F].zipWithIndex[F].drop(1).map((F.div _).tupled)

  def prefixProducts[N](implicit N: Numeric[N]): Process1[N, N] =
    scan(N.one)(N.times)

  def product[N](implicit N: Numeric[N]): Process1[N, N] =
    fold(N.one)(N.times)
}
