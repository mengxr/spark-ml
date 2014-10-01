package ml.algorithm

import ml._
import ml.estimator.Estimator
import ml.evaluation.EvaluationMetric
import ml.transformer.Transformer
import org.apache.spark.sql.SchemaRDD

class CrossValidation[InnerModel <: Transformer](override val id: String)
  extends Estimator[CrossValidation.Model[InnerModel]] {

  def this() = this("CV-" + Identifiable.randomId())

  def fit(dataset: SchemaRDD, paramMap: ParamMap): InnerModel = ???

  // Parameters
  val innerModel: Param[InnerModel] = new Param(this, "innerModel", "inner model to cross-validate", None)

  val evaluator: Param[EvaluationMetric[InnerModel]] = new Param(this, "evaluationMetric", "evaluation metric", None)

}

object CrossValidation {

  class Model[InnerModel](override val id: String) extends Transformer {

    def this() = this("CV-model-" + Identifiable.randomId())

    override def transform(dataset: SchemaRDD, paramMap: ParamMap): SchemaRDD = ???

  }

}
