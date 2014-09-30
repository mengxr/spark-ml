package ml.example

import ml._

import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.Row

class LogisticRegression(override val id: String) extends Estimator {

  def this() = this("lr-" + Identifiable.randomId())

  val maxIter: Param[Int] = new Param(this, "maxIter", "max number of iterations", Some(100))

  val regParam: Param[Double] = new Param(this, "regParam", "regularization constant", Some(0.1))

  override def fit(dataset: Dataset, paramMap: ParamMap): LogisticRegression.Model = {
    val sqlContext = dataset.sqlContext
    import sqlContext._
    val instances = dataset.select('label, 'features).map { case Row(label: Double, features: Vector) =>
      LabeledPoint(label, features)
    }.cache()
    val lr = new LogisticRegressionWithLBFGS
    lr.optimizer
      .setRegParam(paramMap.getOrDefault(regParam))
      .setNumIterations(paramMap.getOrDefault(maxIter))
    val model = lr.run(instances)
    new LogisticRegression.Model(id + ".model", model.weights)
  }
}

object LogisticRegression {
  class Model(override val id: String, weight: Vector) extends Transformer {
    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = {
      null
    }
  }
}
