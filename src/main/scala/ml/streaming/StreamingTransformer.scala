package ml.streaming

import ml._
import org.apache.spark.streaming.dstream.DStream

/**
 * Trait for transformer which uses streaming data.
 */
trait StreamingTransformer {

  def transformOn(dstream: DStream[RowWithSchema], paramMap: ParamMap): DStream[RowWithSchema]

  def transformOn(dstream: DStream[RowWithSchema], paramMaps: Array[ParamMap]): Array[DStream[RowWithSchema]] = {
    paramMaps.map(transformOn(dstream, _))
  }

  // TODO: What about current streaming API: def predictOnValues ?  (could be in StreamingClassificationModel)

}
