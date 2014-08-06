Instance
===

##Motivation

An instance is an observation/example used in training and prediction. In MLlib, this is represented by `LabeledPoint`. It has some limitation to be used in a pipeline.
                                                                                  
1. The label type is fixed to `Double`. In a pipeline, we should allow raw labels, e.g., string typed, and maybe multi-label.
2. The vector is fixed to `Vector`. We should allow data of other types.
3. Weight is not addressed.

Another issue is with transformers. For example, `LabelIndexer` can map labels of arbitrary type to integers in range `[0, numClasses]`. As a transformer, the only thing a `LabedIndexer` should know is how to convert the label and carry over other data in the instance, but not what other part of the data looks like.

##Proposal

~~~
/**
 * Represents a instance in a dataset.
 * @tparam D data type
 * @tparam L label type
 */
abstract class Instance[D, @specialized(Int, Double) L] extends Serializable {

  /**
   * Returns the data stored in this instance.
   */
  def data: D

  /**
   * Returns the label stored in this instance.
   */
  def label: L

  /**
   * Returns the weight associated with this instance.
   */
  def weight: Double = 1.0

  /**
   * Copy this instance with some field values (but their types) changed.
   */
  def copyWith(data: D = data, label: L = label, weight: Double = weight): Instance[D, L]

  /**
   * Copy this instance with new label (possibly a new type).
   */
  def copyWithLabel[L1](label: L1): Instance[D, L1]

  /**
   * Copy this instance with new data (possibly a new type).
   */
  def copyWithData[D1](data: D1): Instance[D1, L]
}
~~~

##Example

The `LabelIndexer` code will look like the following

~~~
class LabelIndexing {

  def fit[L, T <: Instance[L, _]](data: RDD[T]): LabelIndexer[L] = ???
}

class LabeledIndexer[L](labelToIndex: Map[L, Int]) {

  def transform[F, T <: Instance[L, F]](data: RDD[T]): RDD[Instanc[Int, F]] = {
    data.map(i => i.copyWithLabel(labelToIndex(i.label)))
  }
}
~~~
