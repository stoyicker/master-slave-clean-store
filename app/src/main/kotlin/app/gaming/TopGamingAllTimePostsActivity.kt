package app.gaming

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import domain.entity.Post
import kotlinx.android.synthetic.main.include_toolbar.toolbar
import kotlinx.android.synthetic.main.include_top_posts_view.content
import kotlinx.android.synthetic.main.include_top_posts_view.error
import kotlinx.android.synthetic.main.include_top_posts_view.progress
import org.jorge.ms.app.BuildConfig
import org.jorge.ms.app.R



/**
 * An Activity that shows the top posts from /r/gaming.
 */
class TopGamingAllTimePostsActivity : AppCompatActivity() {
    private lateinit var coordinator: TopGamingAllTimePostsCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_gaming)
        // https://kotlinlang.org/docs/tutorials/android-plugin.html#using-kotlin-android-extensions
        val view = TopGamingAllTimePostsView(content, error, progress)
        setSupportActionBar(toolbar)
        coordinator = TopGamingAllTimePostsCoordinator(view)
        TopGamingAllTimePostsContentViewConfig.dumpOnto(view, provideCoordinatorBridgeCallback())
        coordinator.actionLoadNextPage()
    }

    /**
     * This gets called before a configuration change happens, so we use it to prevent leaking
     * the observable in the use case. It does not get called when the process finishes abnormally,
     * bun in that case there is no leak to worry about.
     */
    override fun onDestroy() {
        super.onDestroy()
        coordinator.abortActionLoadNextPage()
    }

    /**
     * Provides a callback to define the responses to certain user interactions.
     */
    private fun provideCoordinatorBridgeCallback()
        = object : BehaviorCallback {
        @SuppressLint("InlinedApi")
        override fun onItemClicked(item: Post) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.detailLink))
            // https://developer.android.com/training/implementing-navigation/descendant.html#external-activities
            if (BuildConfig.VERSION_CODE > Build.VERSION_CODES.LOLLIPOP) {
                @Suppress("DEPRECATION")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            }
            val candidates = this@TopGamingAllTimePostsActivity.packageManager
                    .queryIntentActivities(intent, 0)
            if (candidates.size > 0) {
                startActivity(intent)
            }
        }

        override fun onPageLoadRequested() {
            coordinator.actionLoadNextPage()
        }
    }

    internal companion object {
        /**
         * Safe way to provide an intent to route to this activity. More useful if it were to have
         * parameters for example, but a good idea to have nevertheless.
         * @param context The context to start this activity from.
         */
        fun getCallingIntent(context: Context) = Intent(context, TopGamingAllTimePostsActivity::class.java)
    }
}

/**
 * An interface for the view to communicate with the coordinator.
 */
internal interface BehaviorCallback {
    /**
     * To be called when an item click happens.
     * @param item The item clicked.
     */
    fun onItemClicked(item: Post)

    /**
     * To be called when a page load is requested.
     */
    fun onPageLoadRequested()
}
