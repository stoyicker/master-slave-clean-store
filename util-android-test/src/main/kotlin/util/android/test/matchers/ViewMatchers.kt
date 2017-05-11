package util.android.test.matchers

import android.view.View
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * A Matcher that works around the issue of having another Matcher match more than one target.
 * Adapted from http://stackoverflow.com/a/39756832/2065363
 */
fun withIndex(matcher: Matcher<View>, index: Int): BaseMatcher<View> {
    return object : TypeSafeMatcher<View>() {
        internal var currentIndex = 0

        override fun describeTo(description: Description) {
            description.appendText("with index: ")
            description.appendValue(index)
            matcher.describeTo(description)
        }

        override fun matchesSafely(view: View) = matcher.matches(view) && currentIndex++ == index
    }
}
