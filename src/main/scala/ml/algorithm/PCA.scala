package ml.algorithm

import ml._
import ml.dataset.Dataset
import ml.estimator.Estimator
import ml.transformer.Transformer

class PCA(override val id: String) extends Estimator {

  def this() = this("pca-" + Identifiable.randomId())

  val k: Param[Int] = new Param(this, "k", "rank", Some(10))

  override def fit(dataset: Dataset, paramMap: ParamMap): PCA.Model = null
}

object PCA {

  class Model(override val id: String) extends Transformer {
    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = null
  }
}
