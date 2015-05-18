package ml.dataset

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SchemaRDD}

/**
 * Several default implementations of extracting the needed columns from a Dataset.
 */
object DataExtractor {

  /**
   * Extract "features" and "label" columns from a Dataset.
   */
  def extractLabeledPointRDD(dataset: SchemaRDD): RDD[LabeledPoint] = {
    val instances = dataset.select('label, 'features).map { case Row(label: Double, features: Vector) =>
      LabeledPoint(label, features)
    }
  }

}
