package ml.example

import ml.{Identifiable, Estimator, Param}

class PCA(override val id: String) extends Estimator {

  def this() = this("pca-" + Identifiable.randomId())

  override lazy val params: PCA.ParamSet = new PCA.ParamSet(id)

//  def fit[T](dataset: Dataset[_], paramSet: ParamMap): PCA.Model = {
//    val k = paramSet.get(this.params.k)
//    // do some computation
//    null
//  }
}

object PCA {
  class ParamSet(parent: String) extends ml.ParamSet {
    lazy val k = new Param[Int](parent, "k", "number of components", 10)
  }
}
