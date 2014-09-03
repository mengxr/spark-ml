package ml.example

import ml._

class LogisticRegression(override val id: String) extends Estimator with Params {

  def this() = this("lr-" + Identifiable.randomId())

  override val params = new LogisticRegression.ParamSet(id)

  override def fit(dataset: Dataset, paramMap: ParamMap): Transformer = {
    new LogisticRegression.Model(id)
  }
}

object LogisticRegression {

  class ParamSet(parent: String) extends ml.ParamSet {
    lazy val lambda = new Param[Double](parent, "lambda", "regularization constant", 0.1)
    lazy val regType = new Param[String](parent, "regType", "regularization type", "l1")
  }

  class Model(override val id: String) extends Transformer {
    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = {
      null
    }
  }
}
