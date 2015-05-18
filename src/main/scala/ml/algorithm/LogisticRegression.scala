package ml.algorithm

import ml._
import ml.dataset._
import ml.estimator._
import ml.evaluation.{EvaluationMetric, LogLoss}
import ml.optimization._
import ml.transformer.ProbabilisticClassificationModel
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SchemaRDD}

class LogisticRegression(override val id: String) extends Classifier[LogisticRegression.Model] with IterativeEstimator {

  def this() = this("LR-" + Identifiable.randomId())

  // From Classifier
  override def fit(dataset: SchemaRDD, paramMap: ParamMap): LogisticRegression.Model = {
    // Note: We would actually re-use the IterativeEstimator code.
    val instances: RDD[LabeledPoint] = DataExtractor.extractLabeledPointRDD(dataset).cache()
    val lr = new LogisticRegressionWithLBFGS
    lr.optimizer
      .setRegParam(paramMap.getOrDefault(regularization))
      .setNumIterations(paramMap.getOrDefault(maxIter))
    val model = lr.run(instances)
    new LogisticRegression.Model(id + ".model", model.weights)
  }

  // From IterativeEstimator
  override private[ml] def createSolver(dataset: SchemaRDD, paramMap: ParamMap): LogisticRegression.Solver = {
    new LogisticRegression.Solver(paramMap.getOrDefault(optimizer))
    // also init parameter vector, etc.
  }

  // Parameters
  val regularization: Param[Double] = new Param(this, "regularization", "regularization constant", Some(0.1))

  val optimizer: Param[Optimizer] = new Param(this, "optimizer", "optimization algorithm", Some(new GradientDescent()))

}

object LogisticRegression {

  class Model(override val id: String, val weights: Vector)
    extends Classifier.Model with ProbabilisticClassificationModel {

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
