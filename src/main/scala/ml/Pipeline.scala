package ml

import scala.collection.mutable.ListBuffer

class Pipeline(override val id: String) extends Estimator {

  val components = ListBuffer.empty[Any]

  def this() = this("Pipeline-" + Identifiable.randomId())

  def append(estimator: Estimator): this.type = {
    components += estimator
    this
  }

  def append(transformer: Transformer): this.type = {
    components += transformer
    this
  }

  override def fit(dataset: Dataset, paramMap: ParamMap): Transformer = {
    // Search for last estimator.
    var lastIndexOfEstimator = -1
    components.view.zipWithIndex.foreach { case (component, index) =>
      component match {
        case _: Estimator =>
          lastIndexOfEstimator = index
        case _ =>
      }
    }
    var curDataset = dataset
    val transformers = ListBuffer.empty[Transformer]
    components.view.zipWithIndex.foreach { case (component, index) =>
      component match {
        case estimator: Estimator =>
          val transformer = estimator.fit(dataset, paramMap)
          if (index < lastIndexOfEstimator) {
            curDataset = transformer.transform(curDataset, paramMap)
          }
          transformers += transformer
        case transformer: Transformer =>
          if (index < lastIndexOfEstimator) {
            curDataset = transformer.transform(curDataset, paramMap)
          }
          transformers += transformer
        case _ =>
          throw new IllegalArgumentException
      }
    }

    new Pipeline.Model(transformers.toArray)
  }

  override val params: ParamSet = ParamSet.empty
}

object Pipeline {

  class Model(override val id: String, val transformers: Array[Transformer]) extends Transformer {

    def this(transformers: Array[Transformer]) = this("Pipeline.Model-" + Identifiable.randomId(), transformers)

    override def transform(dataset: Dataset, paramMap: ParamMap): Dataset = {
      transformers.foldLeft(dataset) { (dataset, transformer) =>
        transformer.transform(dataset, paramMap)
      }
    }
  }
}

