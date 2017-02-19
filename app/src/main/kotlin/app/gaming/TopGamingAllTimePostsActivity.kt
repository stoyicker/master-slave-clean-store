package app.gaming

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.include_toolbar.toolbar
import kotlinx.android.synthetic.main.include_top_posts_view.content
import kotlinx.android.synthetic.main.include_top_posts_view.error
import kotlinx.android.synthetic.main.include_top_posts_view.progress
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
        setSupportActionBar(toolbar)
        TopGamingAllTimePostsContentViewConfig.dumpOnto(content)
        coordinator = TopGamingAllTimePostsCoordinator(TopGamingAllTimePostsView(content, error, progress))
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

    internal companion object {
        /**
         * Safe way to provide an intent to route to this activity. More useful if it were to have
         * parameters for example, but a good idea to have nevertheless.
         * @param context The context to start this activity from.
         */
        fun getCallingIntent(context: Context) = Intent(context, TopGamingAllTimePostsActivity::class.java)
    }
}
