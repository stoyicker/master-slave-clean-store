package app.gaming

import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.FrameLayout
import android.widget.TextView
import domain.entity.Post
import org.jorge.ms.app.R
import util.android.HtmlCompat
import util.android.ext.getDimension

/**
 * Configuration for the recycler view holding the post list.
 */
internal class TopGamingAllTimePostsFeatureView(
        view: TopGamingAllTimePostsView,
        private val callback: TopGamingAllTimePostsActivity.BehaviorCallback) {
    private val adapter: Adapter = adapter(callback)

    /**
     * Dumps itself onto the injected view.
     */
    init {
        view.contentView.let { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(endlessLoadListener(recyclerView.layoutManager))
            recyclerView.setHasFixedSize(false)
        }
        view.errorView.setOnClickListener { callback.onPageLoadRequested() }
    }

    /**
     * Requests a filtering command to be performed.
     * @param constraint The constraint for the filtering action.
     */
    internal fun filterView(constraint: CharSequence?) {
        adapter.filter.filter(constraint, null)
    }

    /**
     * Returns an adapter with stable ids that reports user interactions to the provided callback.
     * @return An adapter with stable ids that reports user interactions to the provided callback.
     */
    private fun adapter(callback: TopGamingAllTimePostsActivity.BehaviorCallback) =
            Adapter(callback).also { it.setHasStableIds(true) }

    /**
     * Provides support for the user interaction that requests loading additional items.
     *
     */
    private fun endlessLoadListener(layoutManager: RecyclerView.LayoutManager) =
            object : EndlessLoadListener(layoutManager) {
                override fun onLoadMore() {
                    callback.onPageLoadRequested()
                }
    }
}

/**
 * A very simple adapter backed by a mutable list that exposes a method to add items.
 * An alternative would have been to use the databinding library, but the fact that it does not
 * support merge layouts would make diverse screen support more complicated.
 */
internal class Adapter(private val callback: TopGamingAllTimePostsActivity.BehaviorCallback)
    : RecyclerView.Adapter<Adapter.ViewHolder>(), Filterable {
    private var items = listOf<Post>()
    private var shownItems = emptyList<Post>()
    private lateinit var recyclerView: RecyclerView
    private val filter = RepeatableFilter()

    override fun onAttachedToRecyclerView(target: RecyclerView) {
        recyclerView = target
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_post, parent, false), recyclerView, { callback.onItemClicked(it) })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(shownItems[position])
        onViewHolderBound(holder, position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        // This is used to take the latest valid value in the given payload list
        val combinator: (Bundle, String) -> Unit = { bundle, key ->
            @Suppress("UNCHECKED_CAST")
            bundle.putString(key, (payloads as List<Bundle>).fold(Bundle(), { old, new ->
                val oldTitle = old.getString(key)
                bundle.putString(key, new.getString(key) ?: oldTitle)
                new
            }).getString(key))
        }
        val combinedBundle = Bundle().also { bundle ->
            arrayOf(KEY_TITLE, KEY_SUBREDDIT, KEY_SCORE).forEach {
                combinator(bundle, it)
            }
        }
        // Now combinedBundle contains the latest version of each of the fields that can be updated
        // individually
        holder.renderPartial(combinedBundle, shownItems[position])
        onViewHolderBound(holder, position)
    }

    /**
     * A method to wrap operations that need to happen regardless of how a holder is being bound
     * (as in partial vs full update).
     */
    private fun onViewHolderBound(holder: ViewHolder, position: Int) {
        if (position <= shownItems.size - 1) {
            holder.addBottomMargin()
        }
    }

    override fun getItemCount(): Int = shownItems.size

    /**
     * This implementation is a bit 'meh' because of String (the type of the item id, which is
     * what we use to calculate the item hash code) being a bigger type than Long, the required one.
     */
    override fun getItemId(position: Int): Long = shownItems[position].hashCode().toLong()

    /**
     * Requests a list of items to be added to this adapter. This call re-applies the current
     * filter, which means some of the items passed in to be added will not be shown they don't meet
     * the current filter.
     * @param toAdd The items to add.
     */
    internal fun addItems(toAdd: List<Post>) {
        // If the list is empty we have tried to load a non-existent page, which means we already
        // have all pages. Also there is nothing to add.
        if (toAdd.isNotEmpty()) {
            items = items.plus(toAdd).distinct()
            filter.refresh()
        }
    }

    override fun getFilter() = filter

    /**
     * A filter that keeps track of its last query for repetition.
     */
    internal inner class RepeatableFilter : Filter() {
        private var currentQuery: CharSequence = ""
        private lateinit var diff: DiffUtil.DiffResult

        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            currentQuery = constraint?.trim() ?: ""
            val filteredItems = if (currentQuery.isBlank()) {
                items
            } else {
                items.filter { it.title.contains(currentQuery, true) }
            }
            diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = shownItems.size

                override fun getNewListSize() = filteredItems.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                        shownItems[oldItemPosition].let { (oldId) ->
                            filteredItems[newItemPosition].let { (newId) ->
                                oldId.contentEquals(newId)
                            }
                        }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                        shownItems[oldItemPosition].let { oldItem ->
                            filteredItems[newItemPosition].let { newItem ->
                                oldItem == newItem
                            }
                        }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) =
                        shownItems[oldItemPosition].let { (_, oldTitle, oldSubreddit, oldScore) ->
                            filteredItems[newItemPosition].let {
                                (_, newTitle, newSubreddit, newScore) ->
                                Bundle().apply {
                                    putString(KEY_TITLE, newTitle.takeIf {
                                        !it.contentEquals(oldTitle)
                                    })
                                    putString(KEY_SUBREDDIT, newSubreddit.takeIf {
                                        !it.contentEquals(oldSubreddit)
                                    })
                                    putString(KEY_SCORE, "${newScore.takeIf {
                                        it != oldScore
                                    }}")
                                }
                            }
                        }
            })
            return FilterResults().also {
                it.values = filteredItems
                it.count = filteredItems.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            @Suppress("UNCHECKED_CAST")
            shownItems = results?.values as List<Post>? ?: items
            (recyclerView.layoutParams as FrameLayout.LayoutParams).bottomMargin = 0
            diff.dispatchUpdatesTo(this@Adapter)
        }

        /**
         * Queues a new filtering action with the last query.
         */
        fun refresh() = filter(currentQuery)
    }

    /**
     * Very simple viewholder that sets text and click event handling.
     * @param itemView The view to dump the held data.
     */
    internal class ViewHolder internal constructor(
            itemView: View,
            private val recyclerView: RecyclerView,
            private val onItemClicked: (Post) -> Unit): RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.text_title) as TextView
        private val scoreView: TextView = itemView.findViewById(R.id.text_score) as TextView
        private val subredditView: TextView = itemView.findViewById(R.id.text_subreddit) as TextView

        /**
         * Draw an item.
         * @title The item to draw.
         */
        internal fun render(item: Post) {
            titleView.text = HtmlCompat.fromHtml(item.title)
            subredditView.text = item.subreddit
            scoreView.text = item.score.toString()
            itemView.setOnClickListener { onItemClicked(item) }
        }

        /**
         * Performs partial re-drawing of an item.
         * @param bundle The updates that need to be drawn.
         * @param item The item these updates correspond to.
         */
        internal fun renderPartial(bundle: Bundle, item: Post) {
            bundle.getString(KEY_TITLE).takeIf { it != null }.let { titleView.text =
                    HtmlCompat.fromHtml(it!!) }
            bundle.getString(KEY_SUBREDDIT).takeIf { it != null }.let { subredditView.text = it }
            bundle.getString(KEY_SCORE).takeIf { it != null }.let { scoreView.text = it }
            itemView.setOnClickListener { onItemClicked(item) }
        }

        /**
         * Adds a margin under the recycler view for the progress and error views to show.
         */
        internal fun addBottomMargin() {
            (recyclerView.layoutParams as FrameLayout.LayoutParams).bottomMargin =
                itemView.context.getDimension(R.dimen.footer_padding).toInt()
        }
    }

    private companion object {
        private val KEY_TITLE = "KEY_TITLE"
        private val KEY_SUBREDDIT = "KEY_SUBREDDIT"
        private val KEY_SCORE = "KEY_SCORE"
    }
}

/**
 * @see <a href="https://gist.githubusercontent.com/nesquena/d09dc68ff07e845cc622/raw/e2429b173f75afb408b420ad4088fed68240334c/EndlessRecyclerViewScrollListener.java">Adapted from CodePath</a>
 */
internal abstract class EndlessLoadListener(
        private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {
    private var loading = true
    private var previousTotalItemCount = 0

    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }
        if (!loading && lastVisibleItemPosition == totalItemCount - 1) {
            loading = true
            onLoadMore()
        }
    }

    /**
     * Independent of the layout manager in use.
     */
    private fun findLastVisibleItemPosition() =
        when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is StaggeredGridLayoutManager -> getLastVisibleItem(
                    layoutManager.findLastVisibleItemPositions(null))
            else -> throw IllegalStateException(
                    """Only ${LinearLayoutManager::class.java.name} or
                    ${StaggeredGridLayoutManager::class.java.name}""")
        }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    /**
     * Implement your refresh logic here.
     */
    protected abstract fun onLoadMore()
}
