package ml.estimator

import ml.transformer.Transformer

/**
 * Iterative stateful solver for an estimator.
 * This type of estimator holds state (a fixed dataset and a model) and allows for iterative optimization.
 *
 * This type is distinct from an Optimizer in that an Optimizer has no concept of a model (Transformer);
 * an IterativeSolver can produce a model (Transformer) at any time.
 *
 * This type is not an Estimator; rather, an IterativeEstimator can create an IterativeSolver.
 */
abstract class IterativeSolver[Model <: Transformer] {

  /**
   * Run one step (iteration) of learning.
   * @return  True if the step completed.  False if learning had already finished, or if the step failed.
   *          NOTE: This value allows an algorithm such as LinearRegression to be an IterativeEstimator even when the
   *                underlying optimization could be GradientDescent (iterative) or matrix inversion (non-iterative).
   */
  def step(): Boolean

  /**
   * Get the current model.
   */
  def currentModel(): Model

  /**
   * Number of iterations completed so far.
   */
  def numIterations(): Int

}
