package app.gaming

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
import rx.observers.TestSubscriber

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

internal val SUBSCRIBER_GENERATOR: (TopGamingAllTimePostsCoordinator) -> Subscriber<Post> = {
    object : TestSubscriber<Post>() {
        private val realSubscriberDelegate = PageLoadSubscriber(it)

        override fun onStart() {
            super.onStart()
            realSubscriberDelegate.onStart()
        }

        override fun onNext(post: Post?) {
            super.onNext(post)
            realSubscriberDelegate.onNext(post)
        }

        override fun onError(throwable: Throwable?) {
            super.onError(throwable)
            realSubscriberDelegate.onError(throwable)
        }

        override fun onCompleted() {
            super.onCompleted()
            realSubscriberDelegate.onCompleted()
        }
    }
}
