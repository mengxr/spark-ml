package ml.estimator

import ml.dataset.Dataset
import ml.{Identifiable, ParamMap, Params}

abstract class Estimator[Model] extends Identifiable with Params {

  def fit(dataset: Dataset, paramMap: ParamMap): Model

  def fit(dataset: Dataset, paramMaps: Array[ParamMap]): Array[Model] = {
    paramMaps.map(fit(dataset, _))
  }

  /**
   * Parameter for the output model.
   */
  def model: Params = Params.empty
}
