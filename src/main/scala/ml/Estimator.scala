package ml

abstract class Estimator extends Identifiable with Params {

  def fit(dataset: Dataset, paramMap: ParamMap): Transformer

  def fit(dataset: Dataset, paramMaps: Array[ParamMap]): Array[Transformer] = {
    paramMaps.map(fit(dataset, _))
  }

  /**
   * Parameter for the output model.
   */
  def model: Params = Params.empty
}
