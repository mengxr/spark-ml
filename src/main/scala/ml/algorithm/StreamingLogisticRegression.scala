package ml.algorithm

import ml.dataset.{Row, Dataset}
import ml.transformer.StreamingTransformer
import org.apache.spark.streaming.dstream.DStream

import ml._
import ml.estimator._

class StreamingLogisticRegression(override val id: String)
  extends Classifier[StreamingLogisticRegression.Model] with StreamingEstimator[StreamingLogisticRegression.Model] {

  def this() = this("StreamingLR-" + Identifiable.randomId())

  // From Classifier
  override def fit(dataset: Dataset, paramMap: ParamMap): StreamingLogisticRegression.Model = {
    new StreamingLogisticRegression.Model(this.id)
  }

  // From StreamingEstimator
  override def initWith(paramMap: ParamMap): Unit = ???

  override def fitOn(dstream: DStream[Row]): Unit = ???

  override def currentModel(): StreamingLogisticRegression.Model = ???

  override def numIterations(): Int = ???
}

object StreamingLogisticRegression {

  class Model(override val id: String) extends LogisticRegression.Model with StreamingTransformer {

    def this() = this("StreamingLR-model-" + Identifiable.randomId())

    // From Transformer
    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = ???

    // From StreamingTransformer
    override def transformOn(dstream: DStream[Row], paramMap: ParamMap): DStream[Row] = ???

  }
}
