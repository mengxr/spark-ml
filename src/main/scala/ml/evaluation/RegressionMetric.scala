package ml.evaluation

import ml.estimator.Regressor
import org.apache.spark.sql.SchemaRDD

object SquaredError extends EvaluationMetric[Regressor.Model] {
  def compute(dataset: SchemaRDD, model: Regressor.Model): Double = ???
}

object AbsoluteError extends EvaluationMetric[Regressor.Model] {
  def compute(dataset: SchemaRDD, model: Regressor.Model): Double = ???
}
