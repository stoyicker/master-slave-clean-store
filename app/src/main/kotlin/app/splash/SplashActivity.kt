package app.splash

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

/**
 * A simple activity that acts as a splash screen.
 * Note how, instead of using the content view to set the splash, we just set the splash as
 * background in the theme. This allows it to be shown without having to wait for   the content view
 * to be drawn.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleContentOpening()
    }

    /**
     * Schedules the app content to be shown.
     */
    private fun scheduleContentOpening() {
        Handler().postDelayed({ openContent() }, SHOW_TIME_MILLIS)
    }

    /**
     * Closes the splash and introduces the actual content of the app.
     */
    private fun openContent() {
//      TODO TopPostsActivity.getCallingIntent() yadayada and append flags to not to keep the stack
        supportFinishAfterTransition()
    }

    companion object {
        private const val SHOW_TIME_MILLIS = 2000L
    }
}
