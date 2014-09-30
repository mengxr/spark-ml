package ml.estimator

import ml.dataset.{Row, Dataset}
import ml.transformer.Transformer
import org.apache.spark.mllib.linalg.Vector

import ml._
import ml.evaluation._

abstract class Classifier[Model] extends Estimator[Model] {

}

object Classifier {

  abstract class Model extends Transformer {

    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = ??? // default implementation using predict

    def numClasses: Int

    def predict(instance: Row): Int

    def predictRaw(instance: Row): Vector

  }

  def defaultEvaluator: EvaluationMetric = ZeroOneAccuracy

}
