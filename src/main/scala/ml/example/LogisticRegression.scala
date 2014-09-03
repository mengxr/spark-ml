package ml.example

import ml.{Estimator, Identifiable, Param}

class LogisticRegression(override val id: String) extends Estimator {

  def this() = this("lr-" + Identifiable.randomId())

  lazy val params = new LogisticRegression.ParamSet(id)
}

object LogisticRegression {
  class ParamSet(parent: String) extends ml.ParamSet {
    lazy val lambda = new Param[Double](parent, "lambda", "regularization constant", 0.1)
    lazy val regType = new Param[String](parent, "regType", "regularization type", "l1")
  }
}
