import copy
import math
from datetime import datetime


class Pipeline(object):
    def __init__(self):
        self.elements = []
        self.fitted_elements = []

    def add(self, transform, name = ''):
        p = copy.deepcopy(self)
        p.elements.append([transform, name])
        return p

    def zip(self, other):
        # todo
        return Pipeline()

    def __isModel(self, element):
        """
        Return true if this element has fit() and transform() methods.
        """
        # todo

    def fit(self, dataset):
        ds = dataset
        self.fitted_elements = []
        for element in self.elements:
            if self.__isModel(element):
                fitted_transform = element.fit(ds) # model or fitted transform
                self.fitted_elements.append(fitted_transform)
                ds = fitted_transform.transform(ds)
            else:
                self.fitted_elements.append(0) # dummy value
                ds = element(ds) # transform is a simple functor
        return self

    def predict(self, dataset):
        n = len(self.elements)
        if n != len(self.fitted_elements):
            raise Exception()
        ds = dataset
        for i in range(n):
            if self.__isModel(self.elements[i]):
                ds = self.fitted_elements[i].transform(ds)
            else:
                ds = self.elements[i](ds)
        return ds


class GBTParams(object):
    def __init__(self):
        pass

#######################################

def parse_date(date_str):
    """Return parsed datetime tuple"""
    date_format_str = '%Y-%m-%d %H:%M:%S'
    d = datetime.strptime(date_str, date_format_str)
    return {'year': d.year, 'month': d.month, 'day': d.day,
            'hour': d.hour, 'weekday': d.weekday()}

def process_features(data):
    parsed_date = data['datetime'].apply(parse_date).unpack(column_name_prefix='')
    for col in ['year', 'month', 'day', 'hour', 'weekday']:
        data[col] = parsed_date[col]
    data.hide('datetime')

def process_labels(data):
    for col in ['casual', 'registered', 'count']:
        data['log-' + col] = data[col].apply(lambda x: math.log(1 + x))
        data.setLabel(col)
        data.setLabel('log-' + col)

def fuse_predictions(dataset):
    """
    Fused the prediction of two separately trained models.
    The input models are trained in the log domain.
    Return the combine predictions in the original domain.
    """
    def mergePredictions(p1, p2):
        p = (math.exp(p1)-1) + (math.exp(p2)-1)
        return p if p > 0 else 0
    return dataset.map(lambda (p1, p2): mergePredictions(p1, p2))

# Feature transformation
p0 = Pipeline()         # All columns are treated as features initially.
p0 = p0.add(process_features, 'process_features')   # name 'process_features' is optional
p0 = p0.add(process_labels)

# Split pipeline
gbt_params = GBTParams()
gbt_params.target_column = 'log-casual'
p1 = p0.add(GBT(gbt_params), 'model1')
gbt_params.target_column = 'log-registered'
p2 = p0.add(GBT(gbt_params), 'model2')

# Fuse pipeline
pipeline = p1.zip(p2).add(fuse_predictions)

# Load data, and run pipeline.
training_data = MLUtils.read_csv('train.csv')
test_data = MLUtils.read_csv('test.csv')
pipeline.fit(training_data)
predictions = pipeline.predict(test_data)

###
# Perform hyperparameter search.
param_set = GBT.parameterSet()
param_set.get('tree_params').set('max_depth', [10, 15, 20]).set('min_child_weight', [5, 10, 20])
param_set.set('step_size', 0.05).set('num_iterations', 500)
CVresults = GBT.crossValidate(param_set, training_data, CVfolds=10)
# or: CVresults = GBT.trainAndValidate(param_set, training_data, validation_data)
best_model = CVresults.bestModel()
best_params = CVresults.bestParams()

param_set.set('target_column', 'log-casual')
p1 = p0.add(GBT.crossValidator(param_set), 'model1')
param_set.set('target_column', 'log-registered')
p2 = p0.add(GBT.crossValidator(param_set), 'model2')

# Fuse pipeline
pipeline = p1.zip(p2).add(fuse_predictions)
CVresults = pipeline.crossValidate(training_data, CVfolds=10)  # CV over all combinations of parameters throughout pipeline
# NOTE: pipeline.fit(training_data)  would run CV separately for each CrossValidator in the pipeline.
best_pipeline = CVresults.bestPipeline() # get pipeline with each CrossValidator replaced by a LearningAlgorithm with the best parameters.
best_pipeline.fit(training_data)
predictions = best_pipeline.predict(test_data)

class LearningAlgorithm(object):
    def crossValidate(self, param_set, training_data, CVfolds=10):
        return CrossValidationResults()
    def trainAndValidate(self, param_set, training_data, validation_data):
        return CrossValidationResults()
    def crossValidator(self, param_set):
        return CrossValidator(self, param_set)

class CrossValidationResults(object):
    def __init__(self):
        pass
    def bestModel(self):
        pass
    def bestParams(self):
        pass
    def bestPipeline(self):
        pass

class CrossValidator(object):
    def __init__(self, learning_alg, param_set):
        pass

