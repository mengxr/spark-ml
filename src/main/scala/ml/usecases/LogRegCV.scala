package ml.usecases

import ml._
import ml.estimator._
import ml.dataset.Dataset
import ml.algorithm.{CrossValidation, LogisticRegression}

object LogRegCV {

  def main(args: Array[String]): Unit = {

    val lr = new LogisticRegression()
    val cv = new CrossValidation[LogisticRegression]()
    val paramsBuilder = new ParamGridBuilder()
    paramsBuilder.add(lr.maxIter, 100)
    paramsBuilder.addMulti(lr.regularization, Array(0, 0.1, 1, 10))
    paramsBuilder.add(cv.innerModel, lr)
    paramsBuilder.add(cv.evaluator, LogisticRegression.defaultEvaluator)
    val params: Array[ParamMap] = paramsBuilder.build()

    val dataset: Dataset = ???

    val model = cv.fit(dataset, params)

  }

}
