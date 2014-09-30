package ml.example

import ml._

import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS

class LogisticRegression(override val id: String) extends Estimator {

  def this() = this("lr-" + Identifiable.randomId())

  override def fit(dataset: Dataset, paramMap: ParamMap): LogisticRegression.Model = {
    new LogisticRegression.Model(id)
    dataset.
  }

  val maxIter: Param[Int] = new Param(this, "maxIter", "max number of iterations", Some(100))

  val alpha: Param[Double] = new Param(this, "alpha", "regularization constant", Some(0.1))
}

object LogisticRegression {

  class Model(override val id: String) extends Transformer {
    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = {
      null
    }
  }
}
