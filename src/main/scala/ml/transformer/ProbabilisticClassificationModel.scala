package ml.transformer

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.Row

trait ProbabilisticClassificationModel {

  /**
   * Predict the probability of each label.
   */
  def predictProbabilities(instance: Row): Vector

}
