package util.android.test.matchers

import android.content.Intent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf

/**
 * A matcher for IntentChooser testing.
 * @see <a href="https://developer.android.com/reference/android/content/Intent.html#createChooser(android.content.Intent, java.lang.CharSequence)">
 *     Intent | Android Developers<a/>
 */
fun intentChooser(matcher: Matcher<Intent>): Matcher<Intent> {
    return allOf(hasAction(Intent.ACTION_CHOOSER), hasExtra(`is`(Intent.EXTRA_INTENT), matcher))
}
