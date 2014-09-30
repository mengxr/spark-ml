package ml.evaluation

import ml._
import ml.dataset.Dataset
import ml.transformer.Transformer

trait EvaluationMetric[T <: Transformer] {

  def compute(dataset: Dataset, model: T): Double

}
