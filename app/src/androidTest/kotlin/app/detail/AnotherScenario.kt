package app.detail

import nocucumber.scenario.Scenario

@Scenario(
        name = "another scenario",
        featureNames = arrayOf("feature one"),
        steps = arrayOf("bananas", "step_two"))
interface AnotherScenario
