package ml.evaluation

import org.apache.spark.sql.SchemaRDD

import ml.transformer.Transformer

trait EvaluationMetric[T <: Transformer] {

  def compute(dataset: SchemaRDD, model: T): Double

}
