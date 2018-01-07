package app.detail

import nocucumber.scenario.Scenario

@Scenario(
        name = "my scenario one",
        featureNames = arrayOf("feature one", "feature two"),
        stepId = "a step")
interface MyScenarioOne
