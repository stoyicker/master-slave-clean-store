package app.gaming

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.Toolbar
import org.jorge.ms.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Instrumentation for TopGamingActivityInstrumentation.
 */
internal class TopGamingActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = ActivityTestRule(TopGamingAllTimePostsActivity::class.java)
    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun activityIsShown() {
        onView(withId(android.R.id.content)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun toolbarIsCompletelyShownOnOpening() {
        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
    }

    @Test
    fun goingBackPausesApp() {
        expectedException.expect(NoActivityResumedException::class.java)
        expectedException.expectMessage("Pressed back and killed the app")
        pressBack()
    }

    @Test
    fun scrollingCausesToolbarToCollapseAndRequestsALoad() {

    }

    @Test
    fun clickingCausesIntentToBeCalledIfItCanBeHandled() {

    }

    @Test
    fun clickingCausesDialogToBeShownIfItCanNotBeHandled() {

    }
}
