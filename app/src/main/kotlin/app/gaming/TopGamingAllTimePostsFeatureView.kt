package app.gaming

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import android.widget.ImageView
import android.widget.TextView
import app.common.PresentationPost
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.jorge.ms.app.R

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
    private var items = listOf<PresentationPost>()
    private var shownItems = emptyList<PresentationPost>()
    private lateinit var recyclerView: RecyclerView
    private val filter = RepeatableFilter()

    override fun onAttachedToRecyclerView(target: RecyclerView) {
        recyclerView = target
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_post, parent, false), { callback.onItemClicked(it) })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(shownItems[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        // This is used to take the latest valid value in the given payload list
        val fold: (Bundle, String) -> Unit = { bundle, key ->
            @Suppress("UNCHECKED_CAST")
            bundle.putString(key, (payloads as List<Bundle>).fold(Bundle(), { old, new ->
                val oldTitle = old.getString(key)
                bundle.putString(key, new.getString(key) ?: oldTitle)
                new
            }).getString(key))
        }
        val combinedBundle = Bundle().also { bundle ->
            arrayOf(KEY_TITLE, KEY_SUBREDDIT, KEY_SCORE, KEY_THUMBNAIL).forEach {
                fold(bundle, it)
            }
        }
        // Now combinedBundle contains the latest version of each of the fields that can be updated
        // individually
        holder.renderPartial(combinedBundle, shownItems[position])
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
    internal fun addItems(toAdd: List<PresentationPost>) {
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
                        shownItems[oldItemPosition].let {
                            (_, oldTitle, oldSubreddit, oldScore, oldThumbnail) ->
                            filteredItems[newItemPosition].let {
                                (_, newTitle, newSubreddit, newScore, newThumbnail) ->
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
                                    putString(KEY_THUMBNAIL, newThumbnail.takeIf {
                                        it != oldThumbnail
                                    })
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
            shownItems = results?.values as List<PresentationPost>? ?: items
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
     * @param onItemClicked What to run when a click happens.
     */
    internal class ViewHolder internal constructor(
            itemView: View,
            private val onItemClicked: (PresentationPost) -> Unit)
        : RecyclerView.ViewHolder(itemView), Target {
        private val titleView: TextView = itemView.findViewById(R.id.title_view) as TextView
        private val scoreView: TextView = itemView.findViewById(R.id.score) as TextView
        private val subredditView: TextView = itemView.findViewById(R.id.subreddit) as TextView
        private val thumbnailView: ImageView = itemView.findViewById(R.id.thumbnail) as ImageView

        /**
         * Draw an item.
         * @title The item to draw.
         */
        internal fun render(item: PresentationPost) {
            setTitle(item.title)
            setSubreddit(item.subreddit)
            setScore(item.score)
            setThumbnail(item.thumbnailLink)
            itemView.setOnClickListener { onItemClicked(item) }
        }

        /**
         * Performs partial re-drawing of an item.
         * @param bundle The updates that need to be drawn.
         * @param item The item these updates correspond to.
         */
        internal fun renderPartial(bundle: Bundle, item: PresentationPost) {
            bundle.getString(KEY_TITLE)?.let { setTitle(it) }
            bundle.getString(KEY_SUBREDDIT)?.let { setSubreddit(it) }
            bundle.getString(KEY_SCORE)?.let { setScore(Integer.valueOf(it)) }
            setThumbnail(bundle.getString(KEY_THUMBNAIL))
            itemView.setOnClickListener { onItemClicked(item) }
        }

        /**
         * Updates the layout according to the changes required by a new title.
         * @param title The new title.
         */
        private fun setTitle(title: String) {
            titleView.text = title
            thumbnailView.contentDescription = title.toString()
        }

        /**
         * Updates the layout according to the changes required by a new subreddit.
         * @param name The new subreddit name.
         */
        private fun setSubreddit(name: String) {
            subredditView.text = name
        }

        /**
         * Updates the layout according to the changes required by a new score.
         * @param score The new score.
         */
        private fun setScore(score: Int) {
            scoreView.text = score.toString()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            thumbnailView.visibility = View.GONE
            thumbnailView.setImageDrawable(null)
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) { }

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            thumbnailView.setImageBitmap(bitmap)
            thumbnailView.visibility = View.VISIBLE
        }

        /**
         * Updates the layout according to the changes required by a new thumbnail link.
         * @param thumbnailLink The new thumbnail link, or <code>null</code> if none is applicable.
         */
        private fun setThumbnail(thumbnailLink: String?) {
            if (thumbnailLink != null) {
                Picasso.with(thumbnailView.context).load(thumbnailLink).into(this)
            } else {
                thumbnailView.visibility = View.GONE
                thumbnailView.setImageDrawable(null)
            }
        }
    }

    private companion object {
        private val KEY_TITLE = "KEY_TITLE"
        private val KEY_SUBREDDIT = "KEY_SUBREDDIT"
        private val KEY_SCORE = "KEY_SCORE"
        private val KEY_THUMBNAIL = "KEY_THUMBNAIL"
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
