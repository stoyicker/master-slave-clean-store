package app.splash

import android.content.Intent
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import app.gaming.TopGamingAllTimePostsActivity

/**
 * A simple activity that acts as a splash screen.
 * Note how, instead of using the content view to set the splash, we just set the splash as
 * background in the theme. This allows it to be shown without having to wait for   the content view
 * to be drawn.
 */
class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler

    override fun onResume() {
        super.onResume()
        scheduleContentOpening()
    }

    /**
     * Schedules the app content to be shown.
     */
    private fun scheduleContentOpening() {
        handler = Handler()
        handler.postDelayed({ openContent() }, SHOW_TIME_MILLIS)
    }

    /**
     * Closes the splash and introduces the actual content of the app.
     */
    private fun openContent() {
        val intent = TopGamingAllTimePostsActivity.getCallingIntent(this)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        supportFinishAfterTransition()
    }

    override fun onPause() {
        handler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SHOW_TIME_MILLIS = 2000L
    }
}
