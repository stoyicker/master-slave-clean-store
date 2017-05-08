package app.gaming

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import app.MainApplication
import domain.entity.Post
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_top_posts_view.*
import org.jorge.ms.app.R
import javax.inject.Inject

/**
 * An Activity that shows the top posts from r/gaming.
 */
class TopGamingAllTimePostsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var viewConfig: TopGamingAllTimePostsContentViewConfig
    @Inject
    internal lateinit var coordinator: TopGamingAllTimePostsCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_gaming)
        inject()
        setSupportActionBar(toolbar)
<<<<<<< HEAD
        coordinator = TopGamingAllTimePostsCoordinator(view)
        TopGamingAllTimePostsContentViewConfig.dumpOnto(view, provideCoordinatorBridgeCallback())
        coordinator.actionLoadNextPage(intent.getBooleanExtra(
                TopGamingAllTimePostsActivity.KEY_STARTED_MANUALLY, false))
        intent.putExtra(TopGamingAllTimePostsActivity.KEY_STARTED_MANUALLY, false)
=======
        viewConfig.apply()
        coordinator.actionLoadNextPage()
>>>>>>> 43580ab... Move app to Dagger
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
     * Injects this instance with the corresponding feature component.
     */
    private fun inject() {
        (application as MainApplication).buildTopGamingPostsActivityComponent(
                // https://kotlinlang.org/docs/tutorials/android-plugin.html#using-kotlin-android-extensions
                content, error, progress, this).inject(this)
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

    internal companion object {
        private const val KEY_STARTED_MANUALLY = "KEY_STARTED_MANUALLY"
        /**
         * Safe way to schedule an intent to route to this activity. More useful if it were to have
         * parameters for example, but a good idea to have nevertheless.
         * @param context The context to start this activity from.
         */
        fun getCallingIntent(context: Context): Intent {
            val intent = Intent(context, TopGamingAllTimePostsActivity::class.java)
            intent.putExtra(TopGamingAllTimePostsActivity.KEY_STARTED_MANUALLY, true)
            return intent
        }
    }
}
