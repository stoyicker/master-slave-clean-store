package app.splash

import android.app.Instrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import app.gaming.TopGamingAllTimePostsActivity
import org.junit.Rule
import org.junit.Test

/**
 * Instrumentation for SplashActivity.
 */
internal class SplashActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun activityIsShown() {
        onView(withId(android.R.id.content)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun finishesIntoContent() {
        val activityMonitor = Instrumentation.ActivityMonitor(
                TopGamingAllTimePostsActivity::class.java.name, null, true)
        assert(activityMonitor.waitForActivityWithTimeout(SplashActivity.SHOW_TIME_MILLIS * 2)
                != null)
        assert(activityTestRule.activity.isFinishing)
    }
}
