package ml.transformer

import ml.{Identifiable, ParamMap, Params}
import org.apache.spark.sql.SchemaRDD

abstract class Transformer extends Identifiable with Params {

  def transform(dataset: SchemaRDD, paramMap: ParamMap): SchemaRDD

  def transform(dataset: SchemaRDD, paramMaps: Array[ParamMap]): Array[SchemaRDD] = {
    paramMaps.map(transform(dataset, _))
  }
}
