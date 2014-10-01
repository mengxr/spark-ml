package ml.estimator

import org.apache.spark.sql.SchemaRDD

import ml._
import ml.transformer.Transformer

/**
 * Trait for an iterative estimator which uses a static dataset (not streaming).
 */
trait IterativeEstimator[Model <: Transformer] {

  /**
   * Initialize a solver for this estimator with a dataset.
   * The solver can then do iterative learning and produce a model.
   */
  private[ml] def createSolver(dataset: SchemaRDD, paramMap: ParamMap): IterativeSolver[Model]

  val maxIter: Param[Int] = ???

}
