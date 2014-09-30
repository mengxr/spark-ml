package ml.transformer

import org.apache.spark.mllib.linalg.Vector

import ml.dataset.Row

trait ProbabilisticClassificationModel {

  /**
   * Predict the probability of each label.
   */
  def predictProbabilities(instance: Row): Vector

}
