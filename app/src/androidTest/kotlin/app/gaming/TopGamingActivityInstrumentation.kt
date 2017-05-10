package app.gaming

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.FlakyTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.Toolbar
import android.view.View
import domain.entity.Post
import org.jorge.ms.app.R
import org.junit.After
import org.junit.Rule
import org.junit.rules.ExpectedException
import org.junit.rules.Timeout
import rx.Subscriber
import rx.subjects.PublishSubject
import util.android.test.BinaryIdlingResource
import java.net.UnknownHostException
import kotlin.test.assertEquals

/**
 * IdlingResources are not fully reliable: sometimes the test runs fail to allow the main thread to
 * go idle correctly, which causes problems. Should that be your case, re-run and may the force be
 * with you.
 * Because of the issues this imposes on the build (hook setup mostly), all @Test annotations are
 * commented out by default. You will need to uncomment them so the tests are recognized.
 */
internal class TopGamingActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = ActivityTestRule(TopGamingAllTimePostsActivity::class.java)
    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()
    @JvmField
    @Rule
    val globalTimeout: Timeout = Timeout.seconds(5)

    @After
    fun afterTest() {
        Espresso.unregisterIdlingResources(IDLING_RESOURCE)
    }

    @FlakyTest
//    @Test
    fun activityIsShown() {
        PUBLISH_SUBJECT.onCompleted()
        Espresso.registerIdlingResources(IDLING_RESOURCE)
        onView(withId(android.R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE") }
    }

    @FlakyTest
//    @Test
    fun toolbarIsCompletelyShownOnOpening() {
        PUBLISH_SUBJECT.onCompleted()
        Espresso.registerIdlingResources(IDLING_RESOURCE)
        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
    }

    @FlakyTest
//    @Test
    fun goingBackPausesApp() {
        PUBLISH_SUBJECT.onCompleted()
        Espresso.registerIdlingResources(IDLING_RESOURCE)
        expectedException.expect(NoActivityResumedException::class.java)
        expectedException.expectMessage("Pressed back and killed the app")
        Espresso.pressBack()
    }

    @FlakyTest
//    @Test
    fun openingShowsProgress() {
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Progress visibility was not VISIBLE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Error visibility was not GONE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
    }

    @FlakyTest
//    @Test
    fun onLoadItemsAreShown() {
        PUBLISH_SUBJECT.onNext(Post("Bananas title", "r/bananas", 879, "bananaLink"))
        PUBLISH_SUBJECT.onCompleted()
        Espresso.registerIdlingResources(IDLING_RESOURCE)
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Error visibility was not GONE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
        onView(withText("Bananas title")).check(matches(isDisplayed()))
    }

    @FlakyTest
//    @Test
    fun onFailureErrorIsShown() {
        PUBLISH_SUBJECT.onError(UnknownHostException())
        Espresso.registerIdlingResources(IDLING_RESOURCE)
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Error visibility was not VISIBLE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
    }

    companion object {
        private val IDLING_RESOURCE = BinaryIdlingResource("load")
        internal lateinit var PUBLISH_SUBJECT: PublishSubject<Post>
        internal val SUBSCRIBER_GENERATOR: (TopGamingAllTimePostsCoordinator) -> Subscriber<Post> = {
            object : Subscriber<Post>() {
                private val realSubscriberDelegate = PageLoadSubscriber(it)

                override fun onStart() {
                    realSubscriberDelegate.onStart()
                    IDLING_RESOURCE.setIdleState(false)
                }

                override fun onNext(post: Post?) {
                    realSubscriberDelegate.onNext(post)
                }

                override fun onError(throwable: Throwable?) {
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
