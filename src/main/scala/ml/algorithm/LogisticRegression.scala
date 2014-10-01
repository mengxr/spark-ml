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
  override def fit(dataset: Dataset, paramMap: ParamMap): LogisticRegression.Model = {
    val rdd: RDD[LabeledPoint] = DataExtractor.extractLabeledPointRDD(dataset)
    val lrParams: LogisticRegressionParams = ??? // extract from paramMap
    oldTrainFunction(rdd, lrParams)
  }

  private class LogisticRegressionParams

  private def oldTrainFunction(data: RDD[LabeledPoint], lrParams: LogisticRegressionParams): LogisticRegression.Model =
    new LogisticRegression.Model(id)

  // From IterativeEstimator
  override private[ml] def createSolver(dataset: Dataset, paramMap: ParamMap): LogisticRegression.Solver = {
    new LogisticRegression.Solver(paramMap.getOrDefault(optimizer))
    // also init parameter vector, etc.
  }

  // Parameters
  val regularization: Param[Double] = new Param(this, "regularization", "regularization constant", Some(0.1))

  val optimizer: Param[Optimizer] = new Param(this, "optimizer", "optimization algorithm", Some(new GradientDescent()))

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

  // TODO: Should IterativeSolvers have IDs?
  private[ml] class Solver(val optAlg: Optimizer) extends IterativeSolver[Model] {

    override def step(): Boolean = optAlg.step()

    override def currentModel(): LogisticRegression.Model = ???

    override def numIterations(): Int = ???

  }

}
