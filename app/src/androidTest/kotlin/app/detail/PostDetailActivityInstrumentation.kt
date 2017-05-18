package app.detail

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import app.common.PresentationPost
import org.hamcrest.Matchers.allOf
import org.jorge.ms.app.R
import org.junit.Rule
import org.junit.Test
import util.android.test.matchers.intentChooser

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

//    @Test
//    fun activityIsShown() {
//        //TODO Check here for the correct id(s) onView(withId(R.id.content)).check { view, _ ->
//        //    assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE") }
//    }
//
//    @Test
//    fun toolbarIsCompletelyShownOnOpening() {
//        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
//        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
//        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
//    }

    @Test
    fun pressingShareSendsIntent() {
        Intents.init()
        intending(anyIntent()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        onView(withId(R.id.share)).perform(click())
        intended(intentChooser(allOf(
                hasExtra(Intent.EXTRA_TITLE, ITEM.title))))
        Intents.release()
    }

    private companion object {
        private val ITEM = PresentationPost("id", "title", "sr", 7, "img", "http://www.url.com/")
    }
}
