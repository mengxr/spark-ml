package ml.estimator

import ml._

abstract class ClusteringEstimator extends Estimator {



}

object ClusteringEstimator {

  abstract class Model {

    /** Total number of clusters. */
    def k: Int



  }

}