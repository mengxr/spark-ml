package ml.estimator

import ml._
import ml.evaluation.{EvaluationMetric, SquaredError}
import ml.transformer.Transformer
import org.apache.spark.sql.{Row, SchemaRDD}

abstract class Regressor[Model] extends Estimator[Model] {

}

object Regressor {

  abstract class Model extends Transformer {

    override def transform(dataset: SchemaRDD, paramMap: ParamMap): SchemaRDD = ??? // default implementation using predict

    def predict(instance: Row): Double

  }

  def defaultEvaluator: EvaluationMetric = SquaredError

}
