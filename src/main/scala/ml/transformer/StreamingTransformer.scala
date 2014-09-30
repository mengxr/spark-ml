package ml.transformer

import org.apache.spark.streaming.dstream.DStream

import ml._
import ml.dataset.Row

/**
 * Trait for transformer which uses streaming data.
 */
trait StreamingTransformer {

  def transformOn(dstream: DStream[Row], paramMap: ParamMap): DStream[Row]

  def transformOn(dstream: DStream[Row], paramMaps: Array[ParamMap]): Array[DStream[Row]] = {
    paramMaps.map(transformOn(dstream, _))
  }

  // TODO: What about current streaming API: def predictOnValues ?  (could be in StreamingClassificationModel)

}
