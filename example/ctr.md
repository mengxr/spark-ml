Example: CTR prediction
===

##Datasets

* **impression event**: impressionId, impressionTime, userId, adId, browser, ip
* **click event**: clickId, impressionId, clickTime
* **user record**: userId, userFeatures
* **ad record**: adId, adFeatures

The user/ad records are stored both offline (HDFS) for training and online (K/V) for prediction.

##Offline training

```python
sqlContext.parquetFile("/impression/2014/05").registerTempTable("impression")
sqlContext.parquetFile("/click/2014/05").registerTempTable("click")
sqlContext.parquetFile("/user").registerTempTable("user")
sqlContext.parquetFile("/ad").registerTempTable("ad")

dataset = sqlContext.sql("""
  SELECT impressionTime, browser, ip, userFeatures, adFeatures, clickTime IS NOT NULL AS label
  FROM impression
    LEFT OUTER JOIN click ON impresssion.impressionId = click.impressionId
    JOIN user ON impression.userId = user.userId
    JOIN ad ON impression.adId = ad.adId;""")

stratifiedSampler = StratifiedSampler() # downsample negatives
timeTransformer = TimeTransformer() # compute hour of day
indexer = Indexer() # map categorical values into indices
ipLocator = IpLocator() # map ip to location names
interactor = FeatureInteractor() # compute interaction of features
oneHotEncoder = OneHotEncoder()
fvAssembler = FeatureVectorAssembler() # group features
cv = CrossValidator() # cross-validation
evaluator = BinaryClassificationEvaluator()
lr = LogisticRegression()
ir = IsotonicRegresion() # score calibration
pipeline = Pipeline() # simple pipeline object

lrParamMapsInCV = ParamGridBuilder() # parameter grid for CV
  .addMulti(lr.maxIter, [50, 100])
  .addMulti(lr.lambda, [0.1, 0.01])
  .add(lr.regType, "l2")
  .build()

paramMap = { # global parameters
  stratifiedSampler.stratum: ["label"],
  stratifiedSampler.fractions: {True: 1.0, False: 0.01, None: 1.0},
  stratifiedSampler.seed: 11,
  timeTransformer.output: {"impressionHour": {"input": "impressionTime", "type": "HourOfDay"}},
  ipLocator.output: {"country": {"input": "ip", "type": "country"}},
  indexer.output: {"browserIndex": "browser", "countryIndex": "country"},
  indexer.orderByFreq: true,
  oneHotEncoder.output: {"countries": "countryIndex", "browsers": "browserIndex"},
  oneHotEncoder.deactiveInputFeatures: true,
  interactor.output: {"genderMatch": ["userFeatures$gender", "adFeatures$targetGender"]},
  fvAssembler.output: {"features": ["userFeatures", "adFeatures", "impressionHour", "countries", "browsers"]}
  cv.nFold: 3,
  cv.estimator: lr,
  cv.evaluator: evaluator,
  cv.evaluatorParamMaps: lrParamMapsInCV,
  cv.evaluatorParamMaps: {evaluator.metric: "areaUderROC"},
  ir.output: {"ctr": "prediction"},
  pipeline.steps: [stratifiedSampler, timeTransformer, indexer, ipLocator, interactor, oneHotEncoder, fvAssembler, cv, ir],
  pipeline.ignoreInTransformation: [stratifiedSampler]}

model = pipeline.fit(dataset, pipelineParamMap)
model.saveAsJsonFile("/model")
```

0. The entry point of an offline training pipeline is after joining training events with raw features.
1. `stratifiedSampler` is only used in training.
2. It is easy to remove `ipLocator` or `timeTransformer` from the pipeline: update `pipeline.steps` and then modify `fvAssembler.output`.

##Online prediction

In online prediction, we don't have click info.

```python
model = loadFromJsonFile("/model")
ads = ...
ppcs = ... # pay per click
impression = ...
user = fetchUser(impression.userId)
expectedRevenue = []
for (ad, ppc) in zip(ads, ppc): # in parallel
  instance = {
    "impressionTime": impression.impressionTime,
    "browser": impression.browser,
    "ip": impression.ip,
    "userFeatures": user.userFeatures,
    "adFeatures": ad.adFeatures,
  }
  expectedRevenue.append(model.transform(instance)["ctr"] * ppc)
bestAd = ads[argmax(expectedRevenue)]
...
```
