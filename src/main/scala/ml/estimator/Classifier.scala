package ml.estimator

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.{Row, SchemaRDD}

import ml._
import ml.evaluation._
import ml.transformer.Transformer

abstract class Classifier[Model <: Transformer] extends Estimator[Model] {

}

object Classifier {

  abstract class Model extends Transformer {

    override def transform(dataset: SchemaRDD, paramMap: ParamMap): SchemaRDD = ??? // default implementation using predict

    def numClasses: Int

    def predict(instance: Row): Int

    def predictRaw(instance: Row): Vector

  }

  def defaultEvaluator: EvaluationMetric = ZeroOneAccuracy

}
