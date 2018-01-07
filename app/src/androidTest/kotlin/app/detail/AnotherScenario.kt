package app.detail

import nocucumber.scenario.Scenario

@Scenario(
        name = "another scenario",
        featureNames = arrayOf("feature one"),
        stepId = "bananas")
interface AnotherScenario
