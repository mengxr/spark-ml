package ml.usecases

import ml._
import ml.algorithm.{CrossValidation, LogisticRegression}
import org.apache.spark.sql.SchemaRDD

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

    val dataset: SchemaRDD = ???

    val model = cv.fit(dataset, params)

  }

}
