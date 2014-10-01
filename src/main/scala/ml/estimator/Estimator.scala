package ml.estimator

import org.apache.spark.sql.SchemaRDD

import ml.transformer.Transformer
import ml.{Identifiable, ParamMap, Params}

abstract class Estimator[Model <: Transformer] extends Identifiable with Params {

  def fit(dataset: SchemaRDD, paramMap: ParamMap): Model

  def fit(dataset: SchemaRDD, paramMaps: Array[ParamMap]): Array[Model] = {
    paramMaps.map(fit(dataset, _))
  }

  /**
   * Parameter for the output model.
   */
  def model: Params = Params.empty
}
