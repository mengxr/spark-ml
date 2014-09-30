package ml.evaluation

import ml.dataset.Dataset
import ml.estimator.Regressor

object SquaredError extends EvaluationMetric[Regressor.Model] {
  def compute(dataset: Dataset, model: Regressor.Model): Double = ???
}

object AbsoluteError extends EvaluationMetric[Regressor.Model] {
  def compute(dataset: Dataset, model: Regressor.Model): Double = ???
}
