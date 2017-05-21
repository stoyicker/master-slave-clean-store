package app.detail

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.Toolbar
import android.view.View
import app.common.PresentationPost
import org.hamcrest.Matchers.allOf
import org.jorge.ms.app.R
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import util.android.test.matchers.intentChooser
import kotlin.test.assertEquals

/**
 * Instrumentation tests for PostDetailActivity.
 */
internal class PostDetailActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = object : ActivityTestRule<PostDetailActivity>(
            PostDetailActivity::class.java) {
        override fun getActivityIntent(): Intent {
            return PostDetailActivity.getCallingIntent(InstrumentationRegistry.getTargetContext(),
                    ITEM)
        }
    }

    @Test
    fun activityIsShown() {
        onView(withId(android.R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE") }
    }

    @Test
    fun toolbarIsCompletelyShownOnOpening() {
        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
        onView(withId(R.id.share)).check(completelyDisplayedMatcher)
    }

    @Test
    fun pressingShareSendsIntent() {
        Intents.init()
        intending(anyIntent()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        onView(withId(R.id.share)).perform(click())
        intended(intentChooser(allOf(
                hasExtra(Intent.EXTRA_SUBJECT, ITEM.title),
                hasExtra(Intent.EXTRA_TEXT, ITEM.url)
        )))
        Intents.release()
    }

    @Test
    fun viewIsUpdated() {
        verify(activityTestRule.activity.view).updateContent(ITEM)
    }

    private companion object {
        private val ITEM = PresentationPost("id", "title", "sr", 7, "img", "http://www.url.com/")
    }
}
