package ml.streaming

import ml._
import ml.algorithm.LogisticRegression
import ml.estimator._
import org.apache.spark.sql.SchemaRDD
import org.apache.spark.streaming.dstream.DStream

class StreamingLogisticRegression(override val id: String)
  extends Classifier[StreamingLogisticRegression.Model] with StreamingEstimator[StreamingLogisticRegression.Model] {

  def this() = this("StreamingLR-" + Identifiable.randomId())

  // From Classifier
  override def fit(dataset: SchemaRDD, paramMap: ParamMap): StreamingLogisticRegression.Model = {
    new StreamingLogisticRegression.Model(this.id)
  }

  // From StreamingEstimator
  override def initWith(paramMap: ParamMap): Unit = ???

  override def fitOn(dstream: DStream[RowWithSchema]): Unit = ???

  override def currentModel(): StreamingLogisticRegression.Model = ???

  override def numIterations(): Int = ???
}

object StreamingLogisticRegression {

  class Model(override val id: String) extends LogisticRegression.Model with StreamingTransformer {

    def this() = this("StreamingLR-model-" + Identifiable.randomId())

    // From Transformer
    override def transform(dataset: SchemaRDD, paramMap: ParamMap): SchemaRDD = ???

    // From StreamingTransformer
    override def transformOn(dstream: DStream[RowWithSchema], paramMap: ParamMap): DStream[RowWithSchema] = ???

  }
}
