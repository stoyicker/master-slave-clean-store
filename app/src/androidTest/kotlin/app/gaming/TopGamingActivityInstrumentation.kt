package app.gaming

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.hasData
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import domain.entity.Post
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import rx.Subscriber
import rx.subjects.ReplaySubject
import util.android.test.BinaryIdlingResource
import util.android.test.matchers.withIndex

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
            Espresso.registerIdlingResources(IDLING_RESOURCE)
        }

        override fun afterActivityFinished() {
            super.afterActivityFinished()
            Espresso.unregisterIdlingResources(IDLING_RESOURCE)
        }
    }
    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

//    @Test
//    fun activityIsShown() {
//        SUBJECT = ReplaySubject.create()
//        SUBJECT.onCompleted()
//        launchActivity()
//        onView(withId(android.R.id.content)).check { view, _ ->
//            assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE") }
//    }
//
//    @Test
//    fun toolbarIsCompletelyShownOnOpening() {
//        SUBJECT = ReplaySubject.create()
//        SUBJECT.onCompleted()
//        launchActivity()
//        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
//        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
//        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
//    }
//
//    @Test
//    fun goingBackPausesApp() {
//        SUBJECT = ReplaySubject.create()
//        SUBJECT.onCompleted()
//        launchActivity()
//        expectedException.expect(NoActivityResumedException::class.java)
//        expectedException.expectMessage("Pressed back and killed the app")
//        Espresso.pressBack()
//    }
//
//    @Test
//    fun onLoadItemsAreShown() {
//        SUBJECT = ReplaySubject.create()
//        SUBJECT.onNext(Post("0", "Bananas title", "r/bananas", 879, "bananaLink", "tb"))
//        SUBJECT.onCompleted()
//        launchActivity()
//        onView(withId(R.id.progress)).check { view, _ ->
//            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
//        onView(withId(R.id.error)).check { view, _ ->
//            assertEquals(View.GONE, view.visibility, "Error visibility was not GONE") }
//        onView(withId(R.id.content)).check { view, _ ->
//            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
//        onView(withIndex(withText("Bananas title"), 0)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun onFailureErrorIsShown() {
//        SUBJECT = ReplaySubject.create()
//        SUBJECT.onError(UnknownHostException())
//        launchActivity()
//        onView(withId(R.id.progress)).check { view, _ ->
//            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
//        onView(withId(R.id.error)).check { view, _ ->
//            assertEquals(View.VISIBLE, view.visibility, "Error visibility was not VISIBLE") }
//        onView(withId(R.id.content)).check { view, _ ->
//            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
//    }

    @Test
    fun onItemClickIntentIsFired() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onNext(Post("0", "Bananas title", "r/bananas", 879, "http://www.banan.as", "tb"))
        SUBJECT.onCompleted()
        launchActivity()
        Intents.init()
        intending(anyIntent()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        onView(withIndex(withText("Bananas title"), 0)).perform(click())
        intended(allOf(hasAction(equalTo(Intent.ACTION_VIEW)), hasData("http://www.banan.as")))
        Intents.release()
    }

    /**
     * Launches the activity.
     */
    private fun launchActivity() = activityTestRule.launchActivity(
            TopGamingAllTimePostsActivity.getCallingIntent(InstrumentationRegistry.getContext()))

    companion object {
        private lateinit var IDLING_RESOURCE: BinaryIdlingResource
        internal lateinit var SUBJECT: ReplaySubject<Post>
        internal val SUBSCRIBER_GENERATOR: (TopGamingAllTimePostsCoordinator) -> Subscriber<Post> =
                {
                    object : Subscriber<Post>() {
                        private val realSubscriberDelegate = PageLoadSubscriber(it)

                        override fun onStart() {
                            realSubscriberDelegate.onStart()
                            IDLING_RESOURCE.setIdleState(false)
                        }

                        override fun onNext(post: Post) {
                            realSubscriberDelegate.onNext(post)
                        }

                        override fun onError(throwable: Throwable) {
                            realSubscriberDelegate.onError(throwable)
                            IDLING_RESOURCE.setIdleState(true)
                        }

                        override fun onCompleted() {
                            realSubscriberDelegate.onCompleted()
                            IDLING_RESOURCE.setIdleState(true)
                        }
                    }
        }
    }
}
