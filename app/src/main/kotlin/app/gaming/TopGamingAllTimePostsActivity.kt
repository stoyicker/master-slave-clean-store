package app.gaming

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import app.MainApplication
import app.filter.FilterFeature
import domain.entity.Post
import kotlinx.android.synthetic.main.activity_top_gaming.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_top_posts_view.*
import org.jorge.ms.app.R
import javax.inject.Inject


/**
 * An Activity that shows the top posts from r/gaming.
 */
class TopGamingAllTimePostsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var view: TopGamingAllTimePostsFeatureView
    @Inject
    internal lateinit var coordinator: TopGamingAllTimePostsCoordinator
    private lateinit var filterFeatureDelegate: FilterFeature

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_gaming)
        revealLayout()
        inject()
        setSupportActionBar(toolbar)
        requestLoad()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_gaming, menu)
        filterFeatureDelegate = FilterFeature(this,
                menu.findItem(R.id.search).actionView as SearchView, view)
        filterFeatureDelegate.applyQuery(intent.getStringExtra(KEY_QUERY))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) onSearchRequested()
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        if (isChangingConfigurations) {
            intent.putExtra(KEY_ENABLE_ENTER_ANIMATION, false)
            intent.putExtra(KEY_QUERY, filterFeatureDelegate.query)
        }
        super.onStop()
    }

    /**
     * Requests the next item batch to load.
     */
    private fun requestLoad() {
        coordinator.actionLoadNextPage(intent.getBooleanExtra(
                TopGamingAllTimePostsActivity.KEY_STARTED_MANUALLY, false))
        intent.putExtra(TopGamingAllTimePostsActivity.KEY_STARTED_MANUALLY, false)
    }

    /**
     * Reveals the layout using a circular reveal (if API level allows).
     */
    @SuppressLint("NewApi") // False positive
    private fun revealLayout() {
        root.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && intent.getBooleanExtra(KEY_ENABLE_ENTER_ANIMATION, true)) {
            root.apply {
                post {
                    val cx = width / 2
                    val cy = 0
                    val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
                    ViewAnimationUtils.createCircularReveal(this , cx, cy, 0f, finalRadius).start()
                }
            }
        }
    }

    /**
     * Injects this instance with the corresponding feature component.
     */
    private fun inject() {
        (application as MainApplication).buildTopGamingAllTimePostsFeatureComponent(
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
        private const val KEY_ENABLE_ENTER_ANIMATION = "KEY_ENABLE_ENTER_ANIMATION"
        private const val KEY_QUERY = "KEY_QUERY"
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
