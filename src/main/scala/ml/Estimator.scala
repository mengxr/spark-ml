package ml

trait Estimator extends Identifiable {

//  def fit(dataset: Dataset[T], paramSet: ParamMap): M

//  def fit(dataset: Dataset[T], paramSets: Array[ParamMap]): Array[M]

  val params: ParamSet
}
