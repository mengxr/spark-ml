package ml.estimator

import ml._
import ml.dataset.Dataset

/**
 * Trait for an iterative estimator which uses a static dataset (not streaming).
 */
trait IterativeEstimator[Model] {

  // TODO: This will be a private[ml] interface.

  /**
   * Initialize the estimator with a dataset, after which [[step()]] can be called.
   * This does NO iterations of learning.
   */
  def initWith(dataset: Dataset, paramMap: ParamMap): Unit

  /**
   * Run one step (iteration) of learning.
   * @return  True if the step completed.  False if learning had already finished, or if the step failed.
   *          NOTE: This value allows an algorithm such as LinearRegression to be an IterativeEstimator even when the
   *                underlying optimization could be GradientDescent (iterative) or matrix inversion (non-iterative).
   */
  def step(): Boolean

  /**
   * Execute the given number of steps.
   * @return  The number of steps actually executed.
   */
  def step(numIterationsToExecute: Int): Int = {
    var iter = 0
    while (iter < numIterationsToExecute && step()) {
      iter += 1
    }
    iter
  }

  /**
   * Get the current model.
   */
  def currentModel(): Model

  /**
   * Number of iterations completed so far.
   */
  def numIterations(): Int

  val maxIter: Param[Int] = ???

}
