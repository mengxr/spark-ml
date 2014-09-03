package ml

trait Estimator extends Identifiable {

  def fit(dataset: Dataset, paramMap: ParamMap): Transformer

  def fit(dataset: Dataset, paramMaps: Array[ParamMap]): Array[Transformer] = {
    paramMaps.map(fit(dataset, _))
  }

  val params: ParamSet
}
