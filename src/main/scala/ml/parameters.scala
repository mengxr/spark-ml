package ml

import java.lang.reflect.Modifier

import scala.collection.mutable


/**
 * Describes a parameter.
 * @param parentId parent id
 * @param name parameter name
 * @param doc documentation
 * @param defaultValue default value
 * @tparam T parameter value type
 */
class Param[+T](
    val parentId: String,
    val name: String,
    val doc: String,
    val defaultValue: T) extends Serializable {

  override def toString: String = s"$name: $doc (default: $defaultValue)"
}

abstract class ParamSet {
  override def toString: String = {
    val methods = this.getClass.getMethods
    methods.filter { m =>
        Modifier.isPublic(m.getModifiers) &&
          classOf[Param[_]].isAssignableFrom(m.getReturnType) &&
          m.getParameterTypes.isEmpty
      }.map(m => m.invoke(this).toString)
      .mkString("{", "," ,"}")
  }
}

/**
 * A (param, value) pair.
 */
class ParamPair[+T](val param: Param[T], val value: T)

/**
 * A param -> value map.
 */
class ParamMap private (params: mutable.Map[String, mutable.Map[String, Any]]) extends Serializable {

  def this() = this(mutable.Map.empty[String, mutable.Map[String, Any]])

  /**
   * Puts a new (param, value) pair.
   */
  def put[T](param: Param[T], value: T): this.type = {
    if (!params.contains(param.parentId)) {
      params(param.parentId) = mutable.HashMap.empty[String, Any]
    }
    params(param.parentId) += param.name -> value
    this
  }

  /**
   * Puts a list of (param, value) pairs.
   */
  def putAll(paramPairs: ParamPair[_]*): this.type = {
    paramPairs.foreach(pair => this.put(pair.param, pair.value))
    this
  }

  /**
   * Gets the value of the input parameter or the default value if it doesn't exist.
   */
  def getOrDefault[T](param: Param[T]): T = {
    val value = params.get(param.parentId) match {
      case Some(map) =>
        map.getOrElse(param.name, param.defaultValue)
      case None =>
        param.defaultValue
    }
    value.asInstanceOf[T]
  }

  /**
   * Filter this parameter map for the given parent.
   */
  def filter(parent: String): ParamMap = {
    val map = params.getOrElse(parent, mutable.Map.empty[String, Any])
    new ParamMap(mutable.Map(parent -> map))
  }

  /**
   * Make a deep copy of this parameter set.
   */
  def copy: ParamMap = new ParamMap(params.clone())

  override def toString: String = {
    params.map { case (parentId, map) =>
      s"{$parentId: $map}"
    }.mkString("{", ",", "}")
  }
}

/**
 * Builder for a param grid used in grid search.
 */
class ParamGridBuilder {
  val paramGrid = mutable.Map.empty[Param[_], Iterable[_]]

  def add[T](param: Param[T], value: T): this.type = {
    paramGrid.put(param, Seq(value))
    this
  }

  def addMulti[T, V <: T](param: Param[T], values: Iterable[V]): this.type = {
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
