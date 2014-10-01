package ml.estimator

import ml._
import ml.dataset.Dataset
import ml.transformer.Transformer

/**
 * Trait for an iterative estimator which uses a static dataset (not streaming).
 */
trait IterativeEstimator[Model <: Transformer] {

  /**
   * Initialize a solver for this estimator with a dataset.
   * The solver can then do iterative learning and produce a model.
   */
  private[ml] def createSolver(dataset: Dataset, paramMap: ParamMap): IterativeSolver[Model]

  val maxIter: Param[Int] = ???

}
