package app.gaming

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.Toolbar
import android.view.View
import domain.entity.Post
import org.jorge.ms.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import rx.Subscriber
import util.android.test.BinaryIdlingResource
import kotlin.test.assertEquals

/**
 * Instrumentation for TopGamingActivityInstrumentation. Here we could tests other things like
 * the error and progress views being shown when they are supposed to or the toolbar hiding on
 * scroll. However this would require using mockito, which breaks the dex limit, so we
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
        onView(withId(android.R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE")  }
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
    fun openingShowsProgress() {
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Progress visibility was not VISIBLE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Error visibility was not GONE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
    }

    @Test
    fun onLoadItemsAreShown() {
        Espresso.registerIdlingResources(IDLING_RESOURCE)
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Error visibility was not GONE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
        // TODO Check for data content
        Espresso.unregisterIdlingResources(IDLING_RESOURCE)
    }

//    @Test
//    fun onFailureErrorIsShown() {
//        TEST_OBSERVABLE_FACTORY_METHOD = { _ -> Observable.error(UnknownHostException())}
//        Espresso.registerIdlingResources(IDLING_RESOURCE)
//        onView(withId(R.id.progress)).check { view, _ ->
//            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
//        onView(withId(R.id.error)).check { view, _ ->
//            assertEquals(View.VISIBLE, view.visibility, "Error visibility was not VISIBLE") }
//        onView(withId(R.id.content)).check { view, _ ->
//            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
//        Espresso.unregisterIdlingResources(IDLING_RESOURCE)
//    }
}

private val IDLING_RESOURCE = BinaryIdlingResource("load")
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
