package ml

import scala.collection.mutable.ListBuffer

trait PipelineStage extends Identifiable

class Pipeline(override val id: String) extends Estimator {

  def this() = this("Pipeline-" + Identifiable.randomId())

  val stages: Param[Array[PipelineStage]] =
    new Param[Array[PipelineStage]](this, "stages", "stages of the pipeline", None)

  override def fit(dataset: Dataset, paramMap: ParamMap): Transformer = {
    val theStages = paramMap.getOrDefault(stages)
    // Search for last estimator.
    var lastIndexOfEstimator = -1
    theStages.view.zipWithIndex.foreach { case (stage, index) =>
      stage match {
        case _: Estimator =>
          lastIndexOfEstimator = index
        case _ =>
      }
    }
    var curDataset = dataset
    val transformers = ListBuffer.empty[Transformer]
    theStages.view.zipWithIndex.foreach { case (stage, index) =>
      stage match {
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

  override def params: Array[Param[_]] = Array.empty
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

