package ml

import java.lang.reflect.Modifier

import scala.collection.mutable

/**
 * Describes a parameter key.
 * @param parent parent id
 * @param name parameter name
 * @param doc documentation
 * @param defaultValue default value
 * @tparam T parameter value type
 */
class Param[+T](
    val parent: Identifiable,
    val name: String,
    val doc: String,
    val defaultValue: Option[T]) extends Serializable {

  override def toString: String = s"$name: $doc (default: $defaultValue)"
}

/**
 * Trait for components with parameters.
 */
trait Params {

  /** Returns all parameters. */
  def params: Array[Param[_]] = {
    val methods = this.getClass.getMethods
    methods.filter { m =>
      Modifier.isPublic(m.getModifiers) &&
        classOf[Param[_]].isAssignableFrom(m.getReturnType) &&
        m.getParameterTypes.isEmpty
    }.map(m => m.invoke(this).asInstanceOf[Param[_]])
  }

  /** Gets a param by its name. */
  def getParam(paramName: String): Param[_] = {
    val m = this.getClass.getMethod(paramName, null)
    assert(Modifier.isPublic(m.getModifiers) &&
      classOf[Param[_]].isAssignableFrom(m.getReturnType))
    m.invoke(this).asInstanceOf[Param[_]]
  }

  /** Validate parameters specified by the input parameter map. */
  def validateParams(paramMap: ParamMap): Unit = {}
}

object Params {
  val empty: Params = new Params {
    override def params: Array[Param[_]] = Array.empty
  }
}

/**
 * A param -> value map.
 */
class ParamMap private (params: mutable.Map[Identifiable, mutable.Map[String, Any]]) extends Serializable {

  def this() = this(mutable.Map.empty[Identifiable, mutable.Map[String, Any]])

  /**
   * Puts a new (param, value) pair.
   */
  def put[T](param: Param[T], value: T): this.type = {
    if (!params.contains(param.parent)) {
      params(param.parent) = mutable.HashMap.empty[String, Any]
    }
    params(param.parent) += param.name -> value
    this
  }

  /**
   * Gets the value of the input parameter or the default value if it doesn't exist.
   */
  def getOrDefault[T](param: Param[T]): T = {
    val value = params.get(param.parent) match {
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
  def filter(parent: Identifiable): ParamMap = {
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


