package ml.evaluation

import ml.estimator.Classifier
import ml.transformer.ProbabilisticClassificationModel
import org.apache.spark.sql.SchemaRDD

object ZeroOneAccuracy extends EvaluationMetric[Classifier.Model] {
  def compute(dataset: SchemaRDD, model: Classifier.Model): Double = ???
}

object LogLoss extends EvaluationMetric[ProbabilisticClassificationModel] {
  def compute(dataset: SchemaRDD, model: ProbabilisticClassificationModel): Double = ???
}
