/**
 * Bike sharing example
 */

class Dataset
class LearningAlgorithm

class Pipeline {

  def add(transform: Function1[Dataset, Dataset], name: String = ""): Pipeline = ???

  def add(learner: LearningAlgorithm, name: String = ""): Pipeline = ???

  def zip(other: Pipeline): Pipeline = ???

  def fit(dataset: Dataset): Pipeline = ???

  def predict(dataset: Dataset): Dataset = ???

}

class GBTParams

// Feature transformation
p0 = Pipeline()  // All columns are treated as features initially.
p0 = p0.add(processFeatures, "processFeatures")   // name "processFeatures" is optional
p0 = p0.add(processLabels)

// Split pipeline
gbtParams = GBTParams()
gbtParams.targetColumn = "log-casual"
p1 = p0.add(GBT(gbtParams), "model1")
gbt_params.targetColumn = "log-registered"
p2 = p0.add(GBT(gbtParams), "model2")

// Fuse pipeline
pipeline = p1.zip(p2).add(fusePredictions)

// Load data, and run pipeline.
trainingData = MLUtils.readCSV("train.csv")
testData = MLUtils.readCSV("test.csv")
pipeline.fit(trainingData)
predictions = pipeline.predict(testData)

