class Param:

    def __init__(self, parent, name, doc, defaultValue):
        self.parent = parent
        self.name = name
        self.doc = doc
        self.defaultValue = defaultValue

    def withValue(self, value):
        return ParamPair(self, value)

    def __str__(self):
        return "{0}/{1}: {2} (default: {3})".format(self.parent, self.name, self.doc, self.defaultValue)

    def __repr__(self):
        return "{0}/{1}".format(self.parent, self.name)

class ParamPair:

    def __init__(self, param, value):
        assert isinstance(param, Param)
        self.param = param
        self.value = value

class ParamMap:

    def __init__(self):
        self.params = {}

    def put(self, param, value):
        self.params[param] = value
        return self

    def getOrDefault(self, param):
        return self.params[param] if param in self.params else param.defaultValue

    def copy(self):
        newMap = ParamMap()
        newMap.params = self.params.copy()
        return newMap

    def __repr__(self):
        return self.params.__repr__()

class ParamGridBuilder:

    def __init__(self):
        self.paramGrid = {}

    def add(self, param, value):
        return self.addMulti(param, [value,])

    def addMulti(self, param, values):
        self.paramGrid[param] = values
        return self

    def build(self):
        paramMaps = [ParamMap(),]
        for (param, values) in self.paramGrid.items():
          newParamMaps = []
          for paramMap in paramMaps:
              for v in values:
                  newParamMap = paramMap.copy()
                  newParamMap.put(param, v)
                  newParamMaps.append(newParamMap)
          paramMaps = newParamMaps
        return paramMaps
