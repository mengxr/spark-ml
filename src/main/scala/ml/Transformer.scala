package ml

trait Transformer extends Identifiable {

  def transform(dataset: Dataset, paramMap: ParamMap): Dataset

  def transform(dataset: Dataset, paramMaps: Array[ParamMap]): Array[Dataset] = {
    paramMaps.map(transform(dataset, _))
  }
}
