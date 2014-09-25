Machine Learning Use Cases
===

##Ad CTR prediction

###Datasets

* **impression event**: impressionId, impressionTime, userId, adId, browser, ip
* **click event**: clickId, impressionId, clickTime
* **user record**: userId, userFeatures
* **ad record**: adId, adFeatures

The user/ad records are stored both offline (HDFS) for training and online (K/V) for prediction.

####Offline training

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

stratifiedSampler = StratifiedSampler()
timeTransformer = TimeTransformer()
indexer = Indexer()
ipLocator = IpLocator()
interactor = Interactor()
oneHotEncoder = OneHotEncoder()
fvAssembler = FeatureVectorAssembler()
cv = CrossValidator()
evaluator = BinaryClassificationEvaluator()
lr = LogisticRegression()
ir = IsotonicRegresion()

lrParamMaps = ParamGridBuilder()
  .addMulti(lr.maxIter, [50, 100])
  .addMulti(lr.lambda, [0.1, 0.01])
  .add(lr.regType, "l2")
  .build()

pipelineParamMap = {
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
  cv.evaluatorParamMaps: lrParamMaps,
  cv.evaluatorParamMaps: {evaluator.metric: "areaUderROC"},
  ir.output: {"ctr": "prediction"}}
  
pipeline = Pipeline.create(
  stratifiedSampler,
  timeTransformer,
  indexer,
  ipLocator,
  interactor,
  oneHotEncoder,
  fvAssembler,
  cv,
  ir)

model = pipeline.fit(dataset, pipelineParamMap)
model.saveAsJsonFile("/model")
```

####Online prediction

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
