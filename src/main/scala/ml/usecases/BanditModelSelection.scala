package ml.usecases

import ml.ParamMap
import ml.algorithm.LogisticRegression
import ml.dataset.Dataset
import ml.estimator.Classifier
import ml.evaluation.{ZeroOneAccuracy, EvaluationMetric}

/**
 * Example of optimizer for which a private[ml] interface for IterativeEstimator would be useful.
 */
object BanditModelSelection {

  def main(args: Array[String]): Unit = {

    val numModels = 100
    val paramMaps = new Array[ParamMap](numModels)
    val lrs = new Array[LogisticRegression](numModels)

    val trainingDataset: Dataset = ???

    val valDataset: Dataset = ???

    val evalMetric: EvaluationMetric[Classifier.Model] = ZeroOneAccuracy

    lrs.zip(paramMaps).foreach { case (lr, paramMap) => lr.initWith(trainingDataset, paramMap) }

    for (iter <- Range(0, 1000)) {
      val whichModelToUpdate: Int = {
        // choose a model to update
        // (Actual alg would be smarter, but the point is that the current model may be needed.)
        lrs.zipWithIndex.map { case (lr, lrIndex) =>
          (evalMetric.compute(valDataset, lr.currentModel()), lrIndex)
        }.maxBy(_._1)._2
      }
      lrs(whichModelToUpdate).step()
    }

    val bestModel = ???

    /* TODO: a few main questions here:
     (1) Should we provide an internal (private[mllib]) interface for developers to call step() on models?
          If we do not, then it will be difficult to write adaptive code like that above.  In particular, the code
          above adaptively chooses the number of steps for each model based on how other models are doing.
          Without an interface like this, it would be hard to share information across models.
     (2) If we do provide this interface, should we allow multi-model training?  I vote no, although we could add that
          add that functionality later.
    */

  }

}
