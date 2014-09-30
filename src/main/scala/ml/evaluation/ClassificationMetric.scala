package ml.evaluation

import ml.estimator.Classifier
import ml.dataset.Dataset
import ml.transformer.ProbabilisticClassificationModel

object ZeroOneAccuracy extends EvaluationMetric[Classifier.Model] {
  def compute(dataset: Dataset, model: Classifier.Model): Double = ???
}

object LogLoss extends EvaluationMetric[ProbabilisticClassificationModel] {
  def compute(dataset: Dataset, model: ProbabilisticClassificationModel): Double = ???
}
