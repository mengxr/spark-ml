package ml.streaming

import ml._
import org.apache.spark.streaming.dstream.DStream

/**
 * Trait for an iterative estimator which uses streaming data.
 */
trait StreamingEstimator[Model] {

  def initWith(paramMap: ParamMap): Unit

  def fitOn(dstream: DStream[RowWithSchema]): Unit

  def currentModel(): Model

  def numIterations(): Int
}
