package app.splash

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

/**
 * Instrumentation for SplashActivity.
 */
class SplashActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun activityIsShown() {
        onView(withId(android.R.id.content)).check(matches(isCompletelyDisplayed()))
    }
}
