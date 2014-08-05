Parameters
===

We want to separate parameters from algorithms for easy configuration of a machine learning pipeline. sk-learn's solution is string-based (copied from http://scikit-learn.org/stable/auto_examples/grid_search_text_feature_extraction.html):

~~~
pipeline = Pipeline([
    ('vect', CountVectorizer()),
    ('tfidf', TfidfTransformer()),
    ('clf', SGDClassifier()),
])

parameters = {
    'vect__max_df': (0.5, 0.75, 1.0),
    'vect__max_features': (None, 5000, 10000, 50000),
    'vect__ngram_range': ((1, 1), (1, 2)),  # unigrams or bigrams
    'tfidf__use_idf': (True, False),
    'tfidf__norm': ('l1', 'l2'),
    'clf__alpha': (0.00001, 0.000001),
    'clf__penalty': ('l2', 'elasticnet'),
    'clf__n_iter': (10, 50, 80),
}
~~~

There are several problems of sk-learn's approach:

1. user needs to remember the name for each step and match them in order to set parameters
2. user needs to look up the doc for parameter names unless he/she is very familiar with the algorithm
3. type safety

The proposed interface for parameters:

~~~
/**
 * Describes a parameter.
 * @param parent parent name
 * @param name parameter name
 * @param doc documentation
 * @param defaultValue default value
 * @tparam T parameter value type
 */
class Param[T](
    val parent: String,
    val name: String,
    val doc: String,
    val defaultValue: T) extends Serializable {

  /**
   * Creates a (param, value) pair.
   */
  def withValue(value: T): ParamPair[T] = ???

  override def toString: String = ???
}

/**
 * A (param, value) pair.
 */
class ParamPair[T](val param: Param[T], val value: T)

/**
 * A param -> value map.
 */
class ParamMap private (params: mutable.Map[String, mutable.Map[String, Any]]) extends Serializable {

  def this() = this(mutable.Map.empty[String, mutable.Map[String, Any]])

  /**
   * Puts a new (param, value) pair.
   */
  def put[T](param: Param[T], value: T): this.type = ???

  /**
   * Puts a list of (param, value) pairs.
   */
  def putAll(paramPairs: ParamPair[_]*): this.type = ???

  /**
   * Gets the value of the input parameter or the default value if it doesn't exist.
   */
  def getOrDefault[T](param: Param[T]): T = ???

  /**
   * Filter this parameter map for the given parent.
   */
  def filter(parent: String): ParamMap = ???

  /**
   * Make a deep copy of this parameter set.
   */
  def copy: ParamMap = ???
}

/**
 * Builder for a param grid used in grid search.
 */
class ParamGridBuilder {
  
  /**
   * Adds a param with a single value.
   */
  def add[T](param: Param[T], value: T): this.type = ???

  /**
   * Adds a param with multiple values.
   */
  def addMulti[T, V <: T](param: Param[T], values: Iterable[V]): this.type = ???

  /**
   * Builds the param grid and returns a list of parameter maps.
   */
  def build(): Array[ParamMap] = ???
}
~~~

If we use the proposed parameter design, the equivalent code as in sk-learn's example would be

~~~
val vect = new CountVectorizer()
val tfidf = new TfidfTransformer()
val clf = new SGDClassifier()

val paramGrid: Array[ParamMap] = new ParamGridBuilder()
  .addMulti(vect.params.maxDf, Seq(0.5, 0.75, 1.0)))
  .addMulti(vect.params.maxFeatures, Seq(-1, 5000, 10000, 50000))
  .addMulti(tfidf.params.useIdf, Seq(true, false))
  .add(tfidf.params.norm, "l2")
  .addMulti(clf.params.alpha, Seq(0.0001, 0.00001))
  .build()
~~~

The Python API should follow Scala's.

##Note

An algorithm may be used more than once in a pipeline and we need to distinguish their parameters.

~~~
val norm1 = new Normalizer()
val norm2 = new Normalizer()

val paramMap = new ParamMap()
  .put(norm1.params.norm, "l1")
  .put(norm2.params.norm, "l2")
~~~

The proposed solution is to attach each normalizer a name, a random name like "normalizer-#####" is used by default.