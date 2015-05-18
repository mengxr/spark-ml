package ml

import scala.collection.mutable

/**
 * Builder for a param grid used in grid search.
 */
class ParamGridBuilder {
  val paramGrid = mutable.Map.empty[Param[_], Iterable[_]]

  def add[T](param: Param[T], value: T): this.type = {
    paramGrid.put(param, Seq(value))
    this
  }

  def addMulti[T](param: Param[T], values: Iterable[T]): this.type = {
    paramGrid.put(param, values)
    this
  }

  def build(): Array[ParamMap] = {
    var paramSets = Array(new ParamMap)
    paramGrid.foreach { case (param, values) =>
      val newParamSets = values.flatMap { v =>
        paramSets.map(_.copy.put(param, v))
      }
      paramSets = newParamSets.toArray
    }
    paramSets
  }
}
