Bike Sharing Workflow - Joseph
===

## Use Cases

* It would be ideal to treat pipelines similarly to RDDs.

### Use case: split and join pipeline (e.g., for bike sharing)

* Load data
* Transform data
* Fit 2 models on data
* Join predictions

```
p0 = new Pipeline()
p0 ::= (transform, 'transform') // name is optional
p1 = p0 :: (model1, 'model1')
p2 = p0 :: (model2, 'model2')
pipeline = p1.zip(p2) :: FusePredictions // Functor FusePredictions can use names 'model1' and 'model2'
                                         //  or could use ordering of zipped pipelines (as in RDD.zip).

// outputs nothing, but fits both models
pipeline.fit(trainData) = {
  tmp = transform(trainData)
  model1.fit(tmp)
  model2.fit(tmp)
}

// outputs Dataset of labels
pipeline.predict(testData) = {
  tmp = transform(testData)
  tmp1 = model1.predict(tmp)
  tmp2 = model2.predict(tmp)
  FusePredictions(tmp1, tmp2) // outputs Dataset of predictions
}
```

### Use case: multi-view learning

* Load trainData1, trainData2, testData
* Fit model1 on trainData1
* Fit model2 on trainData2
* Fuse predictions of model1, model2 on testData

```
p1 = new Pipeline('p1') :: (model1, 'model1')
p2 = new Pipeline('p2') :: (model2, 'model2')
pipeline = p1.zip(p2) :: FusePredictions

pipeline.fit(trainData1, trainData2) = { // ordering is same as given by the pipeline
  // May also use labels:
  //  pipeline.fit(Map('p1' -> trainData1, 'p2' -> trainData2))
  p1.fit(trainData1)
  p2.fit(trainData2)
}
```

### Use case: multiple models in sequence

* Load trainData, testData
* Fit model1 on trainData
* Fit model2 on trainData + output of model 1
* Predict on testData using model1, then model2

```
p0 = new Pipeline()
p1 = p0 :: (model1, 'model1') :: TransformToFeature
pipeline = p0.zip(p1)
pipeline ::= (model2, 'model2')

pipeline.fit(trainData) = {
  model1.fit(trainData)
  tmp = model1.predict(trainData).markAsFeature()
  tmp2 = trainData.zip(tmp)
  model2.fit(tmp2)
}
pipeline.predict(testData) = {
  tmp = model1.predict(trainData).markAsFeature()
  tmp2 = trainData.zip(tmp)
  model2.predict(tmp2)
}
```

### Pipelines

Model:

* .fit(Dataset)
* .transform(Dataset): Dataset

Pipeline:

* .add(functor: Function[Dataset, Dataset])
* .add(model: Model)
* .fit(Dataset): Dataset
	* For functors f, call f()
	* For models m, call m.fit()
* .predict(Dataset): Dataset
	* For functors f, call f()
	* For models m, call m.fit() and m.predict()/transform()

3 column types: label, feature, hidden

* default for loaded datasets = feature
* default for predictions = label


## GraphLab (Python)

```
import math
from datetime import datetime
import graphlab as gl

def parse_date(date_str):
    """Return parsed datetime tuple"""
    date_format_str = '%Y-%m-%d %H:%M:%S'
    d = datetime.strptime(date_str, date_format_str)
    return {'year': d.year, 'month': d.month, 'day': d.day,
            'hour': d.hour, 'weekday': d.weekday()}

def process_date_column(data_sframe):
    """Split the 'datetime' column of a given sframe"""
    parsed_date = data_sframe['datetime'].apply(parse_date).unpack(column_name_prefix='')
    for col in ['year', 'month', 'day', 'hour', 'weekday']:
        data_sframe[col] = parsed_date[col]

# load data
training_sframe = gl.SFrame.read_csv('train.csv')
test_sframe = gl.SFrame.read_csv('test.csv')

process_date_column(training_sframe)
process_date_column(test_sframe)

# Create three new columns: log-casual, log-registered, and log-count
for col in ['casual', 'registered', 'count']:
    training_sframe['log-' + col] = training_sframe[col].apply(lambda x: math.log(1 + x))


features = ['datetime', 'season', 'holiday', 'workingday', 'weather',
            'temp', 'atemp', 'humidity', 'windspeed']
new_features = features + ['year', 'month', 'weekday', 'hour']
new_features.remove('datetime')

m1 = gl.boosted_trees.create(training_sframe,
                             feature_columns=new_features,
                             target_column='log-casual']

m2 = gl.boosted_trees.create(training_sframe,
                             feature_columns=new_features,
                             target_column='log-registered')

def fused_predict(m1, m2, test_sframe):
    """
    Fused the prediction of two separately trained models.
    The input models are trained in the log domain.
    Return the combine predictions in the original domain.
    """
    p1 = m1.predict(test_sframe).apply(lambda x: math.exp(x)-1)
    p2 = m2.predict(test_sframe).apply(lambda x: math.exp(x)-1)
    return (p1 + p2).apply(lambda x: x if x > 0 else 0)

prediction = fused_predict(m1, m2, test_sframe)

```
