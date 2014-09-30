package ml.estimator

import ml._
import ml.dataset.{Row, Dataset}
import ml.evaluation.{SquaredError, EvaluationMetric}
import ml.transformer.Transformer

abstract class Regressor[Model] extends Estimator[Model] {

}

object Regressor {

  abstract class Model extends Transformer {

    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = ??? // default implementation using predict

    def predict(instance: Row): Double

  }

  def defaultEvaluator: EvaluationMetric = SquaredError

}
