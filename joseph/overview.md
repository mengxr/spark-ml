MLlib API Design Overview
===

## Main Items

* Class hierarchy for learning algorithms
* Main types
	* Learning algorithm
	* Model
	* Parameters and parameter sets
	* Dataset
	* Data transforms
	* Pipeline
	* Other concepts?
		* Losses/objectives
* Standardized utility functions
	* Q: Do utility functions (e.g., accuracy) belong to a class (Classifier) or to a utility object?


## Main Types

### Dataset

* Support labels for columns and for groups of columns.
	* This will be useful in pipelines to allow labeling of the two parts of zipped pipelines.  (See bike sharing example.)
* example weights

### Pipeline

Pipeline elements:

* Transform: functor which is not fitted
	* operator()(Dataset): Dataset
		* (synonym) transform(Dataset): Dataset
* FittableTransform: functor which is fitted
	* fit(Dataset): Transform
* Model
	* transform(Dataset): Dataset
		* (synonym) predict(Dataset): Dataset
* LearningAlgorithm
	* fit(Dataset): Model


Questions:

* Should Pipeline inherit from LearningAlgorithm?
	* If so, then a fitted Pipeline should inherit from Model.
* Should cross validation with a pipeline do multiplicative or additive CV by default?
	* Multiplicative: Iterate over the cross product of all parameters sets for all learning algorithms in the pipeline.
	* Additive: Do CV for each learning algorithm separately (in pipeline order).

