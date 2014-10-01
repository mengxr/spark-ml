package ml.algorithm

import org.apache.spark.mllib.linalg.Vector

import ml._
import ml.estimator.{IterativeSolver, Classifier, IterativeEstimator}
import ml.dataset.{Row, Dataset}
import ml.evaluation.{EvaluationMetric, ZeroOneAccuracy}
import ml.transformer._

class AdaBoost(override val id: String) extends Classifier[AdaBoost.Model] with IterativeEstimator[AdaBoost.Model] {

  def this() = this("AdaBoost-" + Identifiable.randomId())

  // From Classifier
  override def fit(dataset: Dataset, paramMap: ParamMap): Classifier.Model =
    new AdaBoost.Model(Array.empty, Array.empty, this.id)

  // From IterativeEstimator
  override private[ml] def createSolver(dataset: Dataset, paramMap: ParamMap): AdaBoost.Solver = ???

  // Parameters
  val weakLearner: Param[Classifier] =
    new Param(this, "weakLearner", "weak learning algorithm", Some(new LogisticRegression()))
}

object AdaBoost {

  class Model(
      val weakHypotheses: Array[Classifier.Model],
      val weakHypothesisWeights: Array[Double],
      override val id: String)
    extends Classifier.Model with ProbabilisticClassificationModel {

    def this(weakHypotheses: Array[Classifier.Model], weakHypothesisWeights: Array[Double]) =
      this("AdaBoost-model-" + Identifiable.randomId())

    require(weakHypotheses.size != 0)
    require(weakHypotheses.size == weakHypothesisWeights.size)

    // From Classifier.Model:
    override val numClasses: Int = weakHypotheses(0).numClasses

    require(weakHypotheses.forall(_.numClasses == numClasses))

    override def predict(instance: Row): Int = ???

    override def predictRaw(instance: Row): Vector = ???

    // predictProbabilities applicable to AdaBoost but not all boosting algorithms (depending on loss)
    override def predictProbabilities(instance: Row): Vector = ???
  }

  def defaultEvaluator: EvaluationMetric = ZeroOneAccuracy

  private[ml] class Solver extends IterativeSolver[Model] {

    override def step(): Boolean = ???

    override def currentModel(): LogisticRegression.Model = ???

    override def numIterations(): Int = ???
  }

}
