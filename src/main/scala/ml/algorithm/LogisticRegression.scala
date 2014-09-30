package ml.algorithm

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

import ml._
import ml.dataset.{DataExtractor, Row, Dataset}
import ml.estimator._
import ml.evaluation.{EvaluationMetric, LogLoss}
import ml.optimization._
import ml.transformer.{Transformer, ProbabilisticClassificationModel}

class LogisticRegression(override val id: String) extends Classifier[LogisticRegression.Model] with IterativeEstimator {

  def this() = this("LR-" + Identifiable.randomId())

  // From Classifier
  /**
   * This initializes this estimator with the given dataset and runs learning until completion.
   * If this estimator has been initialized with another Dataset and model, that Dataset and model are discarded.
   */
  override def fit(dataset: Dataset, paramMap: ParamMap): LogisticRegression.Model = {
    val rdd: RDD[LabeledPoint] = DataExtractor.extractLabeledPointRDD(dataset)
    val lrParams: LogisticRegressionParams = ??? // extract from paramMap
    oldTrainFunction(rdd, lrParams)
  }

  private class LogisticRegressionParams

  private def oldTrainFunction(data: RDD[LabeledPoint], lrParams: LogisticRegressionParams): LogisticRegression.Model =
    new LogisticRegression.Model(id)

  // From IterativeEstimator
  /**
   * This re-initializes learning with a new dataset (and model).
   */
  override def initWith(dataset: Dataset, paramMap: ParamMap): Unit = {
    _optAlg = paramMap.getOrDefault(optimizer)
    // also init parameter vector, etc.
  }

  override def step(): Boolean = _optAlg.step()

  override def currentModel(): LogisticRegression.Model = ???

  override def numIterations(): Int = ???

  // Parameters
  val regularization: Param[Double] = new Param(this, "regularization", "regularization constant", Some(0.1))

  val optimizer: Param[Optimizer] = new Param(this, "optimizer", "optimization algorithm", Some(new GradientDescent()))

  var _optAlg: Optimizer = null

}

object LogisticRegression {

  class Model(override val id: String) extends Classifier.Model with ProbabilisticClassificationModel {

    def this() = this("LR-model-" + Identifiable.randomId())

    override val numClasses: Int = ???

    override def predict(instance: Row): Int = ???

    override def predictRaw(instance: Row): Vector = ???

    override def predictProbabilities(instance: Row): Vector = ???

  }

  def defaultEvaluator: EvaluationMetric = LogLoss

}
