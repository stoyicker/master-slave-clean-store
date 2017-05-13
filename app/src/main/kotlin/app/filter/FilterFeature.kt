package app.filter

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import app.gaming.TopGamingAllTimePostsFeatureView
import org.jorge.ms.app.R
import util.android.HtmlCompat

/**
 * Contains boilerplate for real-time search.
 */
internal class FilterFeature(activity: Activity, private val searchView: SearchView,
                             private val target: TopGamingAllTimePostsFeatureView) {
    internal var query: CharSequence = ""
        private set

    init {
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    this@FilterFeature.query = newText ?: ""
                    target.filterView(query)
                    return true
                }
            })
            setSearchableInfo((context.getSystemService(Context.SEARCH_SERVICE) as SearchManager)
                    .getSearchableInfo(activity.componentName))
            setIconifiedByDefault(false)
            queryHint = HtmlCompat.fromHtml(context.getString(R.string.search_view_hint))
        }
        activity.setDefaultKeyMode(AppCompatActivity.DEFAULT_KEYS_SEARCH_LOCAL)
    }

    /**
     * Delegates a query to the query handler in order to filter the list.
     * @param newQuery The query.
     */
    internal fun applyQuery(newQuery: CharSequence?) {
        searchView.setQuery(newQuery, false)
    }
}
