package ml.estimator

import ml.dataset.Row
import ml.transformer.Transformer
import org.apache.spark.streaming.dstream.DStream

import ml._

/**
 * Trait for an iterative estimator which uses streaming data.
 */
trait StreamingEstimator[Model] {

  def initWith(paramMap: ParamMap): Unit

  def fitOn(dstream: DStream[Row]): Unit

  def currentModel(): Model

  def numIterations(): Int
}
