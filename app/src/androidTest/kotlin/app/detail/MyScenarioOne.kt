package app.detail

import nocucumber.scenario.Scenario

@Scenario(
        name = "my scenario one",
        featureNames = arrayOf("feature one", "feature two"),
        steps = arrayOf("step one", "step two", "bananas"))
interface MyScenarioOne
