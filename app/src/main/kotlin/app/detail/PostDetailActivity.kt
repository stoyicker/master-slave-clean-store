package app.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import app.common.PresentationPost
import app.share.ShareFeature
import kotlinx.android.synthetic.main.include_toolbar.*
import org.jorge.ms.app.R


/**
 * Activity that shows a post in detail.
 */
internal class PostDetailActivity : AppCompatActivity() {
//    @Inject
//    lateinit var view: PostDetailView
    private val shareFeatureDelegate = ShareFeature(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
        configureToolbar()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
//        intent?.getParcelableExtra<PresentationPost>(KEY_MODEL)?.let { view.update(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.post_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            R.id.share -> {
                intent?.getParcelableExtra<PresentationPost>(KEY_MODEL)?.let {
                    shareFeatureDelegate.share(it)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    internal companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val KEY_MODEL = "KEY_MODEL"

        /**
         * Safe way to obtain an intent to route to this activity. More useful if it were to have
         * parameters for example, but a good idea to have nevertheless.
         * @param context The context to start this activity from.
         */
        fun getCallingIntent(context: Context, model: PresentationPost): Intent {
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra(KEY_MODEL, model)
            return intent
        }
    }
}
