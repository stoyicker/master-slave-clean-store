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
import android.view.View
import org.jorge.ms.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Instrumentation for TopGamingActivityInstrumentation. Here we could tests other things like
 * the error and progress views being shown when they are supposed to or the toolbar hiding on
 * scroll. However this would require using mockito, which breaks the dex limit, so we would
 * need a new flavor and some additional setup to ensure that the main flavor does not get
 * multidexed so I am leaving it aside.
 */
internal class TopGamingActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = ActivityTestRule(TopGamingAllTimePostsActivity::class.java)
    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    internal fun activityIsShown() {
        onView(withId(android.R.id.content)).check { view, _ -> view.visibility = View.VISIBLE }
    }

    @Test
    internal fun toolbarIsCompletelyShownOnOpening() {
        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
    }

    @Test
    internal fun goingBackPausesApp() {
        expectedException.expect(NoActivityResumedException::class.java)
        expectedException.expectMessage("Pressed back and killed the app")
        pressBack()
    }

    @Test
    internal fun openingShowsProgress() {
        onView(withId(R.id.progress)).check { view, _ -> view.visibility = View.VISIBLE }
        onView(withId(R.id.error)).check { view, _ -> view.visibility = View.GONE }
        onView(withId(R.id.content)).check { view, _ -> view.visibility = View.GONE }
    }
}
