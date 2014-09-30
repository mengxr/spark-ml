package ml.dataset

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Several default implementations of extracting the needed columns from a Dataset.
 */
object DataExtractor {

  /**
   * Extract "features" and "label" columns from a Dataset.
   */
  def extractLabeledPointRDD(dataset: Dataset): RDD[LabeledPoint] = ???

}
