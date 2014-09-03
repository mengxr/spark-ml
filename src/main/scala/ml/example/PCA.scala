package ml.example

import ml._

class PCA(override val id: String) extends Estimator with Params {

  def this() = this("pca-" + Identifiable.randomId())

  override val params: PCA.ParamSet = new PCA.ParamSet(id)

  override def fit(dataset: Dataset, paramMap: ParamMap): Transformer = null
}

object PCA {
  class ParamSet(parent: String) extends ml.ParamSet {
    lazy val k = new Param[Int](parent, "k", "number of components", 10)
  }

  class Model(override val id: String) extends Transformer {
    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = null
  }
}
