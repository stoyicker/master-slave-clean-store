package app.gaming

import android.app.Activity
import android.app.Instrumentation
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.Toolbar
import android.view.View
import app.common.PresentationPost
import app.detail.PostDetailActivity
import domain.entity.Post
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.ReplaySubject
import org.hamcrest.Matchers.allOf
import org.jorge.ms.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import util.android.test.BinaryIdlingResource
import util.android.test.matchers.withIndex
import java.net.UnknownHostException
import kotlin.test.assertEquals

/**
 * The setup seems a bit strange, but there is a reason: we need to define SUBJECT before
 * the activity is launched since injection happens on launch. Also,
 * ActivityTestRule#beforeActivityLaunched is only called when the Activity is scheduled for launch
 * already, and JUnit's @Before is invoked before the test, but with the Activity prepared already.
 * This forces us to manually launch the activity in every test.
 */
internal class TopGamingActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = object : ActivityTestRule<TopGamingAllTimePostsActivity>(
            TopGamingAllTimePostsActivity::class.java, false, false) {
        override fun beforeActivityLaunched() {
            IDLING_RESOURCE = BinaryIdlingResource("load")
            IdlingRegistry.getInstance().register(IDLING_RESOURCE)
        }

        override fun afterActivityFinished() {
            super.afterActivityFinished()
            IdlingRegistry.getInstance().unregister(IDLING_RESOURCE)
        }
    }
    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun activityIsShown() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onComplete()
        launchActivity()
        onView(withId(android.R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE") }
    }

    @Test
    fun toolbarIsCompletelyShownOnOpening() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onComplete()
        launchActivity()
        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
    }

    @Test
    fun goingBackPausesApp() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onComplete()
        launchActivity()
        expectedException.expect(NoActivityResumedException::class.java)
        expectedException.expectMessage("Pressed back and killed the app")
        Espresso.pressBack()
    }

    @Test
    fun onLoadItemsAreShown() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onNext(setOf(Post(
                "0",
                "Bananas title",
                "r/bananas",
                879,
                "tb",
                "link")))
        SUBJECT.onComplete()
        launchActivity()
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Error visibility was not GONE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
        onView(withIndex(withText("Bananas title"), 0)).check(matches(isDisplayed()))
    }

    @Test
    fun onFailureErrorIsShown() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onError(UnknownHostException())
        launchActivity()
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Error visibility was not VISIBLE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
    }

    @Test
    fun onItemClickDetailIntentIsLaunched() {
        val srcPost = Post(
                "0",
                "Bananas title",
                "r/bananas",
                879,
                "tb",
                "link")
        SUBJECT = ReplaySubject.create()
        SUBJECT.onNext(setOf(srcPost))
        SUBJECT.onComplete()
        launchActivity()
        Intents.init()
        intending(anyIntent()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        onView(withIndex(withText(srcPost.title), 0)).perform(click())
        intended(allOf(hasComponent(PostDetailActivity::class.java.name),
                hasExtra(PostDetailActivity.KEY_MODEL, PresentationPost(
                srcPost.id, srcPost.title, srcPost.subreddit, srcPost.score, srcPost.thumbnailLink,
                        srcPost.url))
        ))
        Intents.release()
    }

    /**
     * Launches the activity.
     */
    private fun launchActivity() = activityTestRule.launchActivity(TopGamingAllTimePostsActivity
            .getCallingIntent(InstrumentationRegistry.getTargetContext()))

    companion object {
        private lateinit var IDLING_RESOURCE: BinaryIdlingResource
        internal lateinit var SUBJECT: ReplaySubject<Iterable<Post>>
        internal val SUBSCRIBER_GENERATOR:
                (TopGamingAllTimePostsCoordinator) -> DisposableSingleObserver<Iterable<Post>> =
                {
                    object : PageLoadSubscriber(it) {
                        override fun onStart() {
                            super.onStart()
                            IDLING_RESOURCE.setIdleState(false)
                        }

                        override fun onSuccess(payload: Iterable<Post>) {
                            super.onSuccess(payload)
                            IDLING_RESOURCE.setIdleState(true)
                        }

                        override fun onError(throwable: Throwable) {
                            super.onError(throwable)
                            IDLING_RESOURCE.setIdleState(true)
                        }
                    }
        }
    }
}
