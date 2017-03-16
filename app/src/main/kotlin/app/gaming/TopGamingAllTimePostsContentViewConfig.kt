package app.gaming

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import util.android.ext.getDimension
import util.android.ext.isPortrait
import domain.entity.Post
import org.jorge.ms.app.R

/**
 * Configures the recycler view holding the post list.
 */
internal object TopGamingAllTimePostsContentViewConfig {
    /**
     * Configures a view.
     * @recyclerView The view to configure.
     */
    internal fun dumpOnto(view: TopGamingAllTimePostsView, callback: BehaviorCallback) {
        view.contentView.let { recyclerView ->
            recyclerView.adapter = provideAdapter(callback)
            recyclerView.layoutManager = provideLayoutManager(recyclerView.context)
            recyclerView.addOnScrollListener(provideEndlessLoadListener(
                    recyclerView.layoutManager, callback))
            recyclerView.setHasFixedSize(false)
        }
        view.errorView.setOnClickListener { callback.onPageLoadRequested() }
    }

    /**
     * Provides an adapter with stable ids.
     * @param callback The callback to feed events back to the coordinator.
     */
    private fun provideAdapter(callback: BehaviorCallback)
            : RecyclerView.Adapter<Adapter.ViewHolder> {
        val adapter = Adapter(object : OnItemClickListener<Post> {
            override fun onItemClicked(item: Post) {
                callback.onItemClicked(item)
            }
        })
        adapter.setHasStableIds(true)
        return adapter
    }

    /**
     * Provides a layout manager based on the size of the screen.
     * @param context The context
     */
    private fun provideLayoutManager(context: Context): RecyclerView.LayoutManager
        = if (context.isPortrait()) {
            LinearLayoutManager(context)
        } else {
            val ret = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            ret.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            ret
        }
    }

    private fun provideEndlessLoadListener(layoutManager: RecyclerView.LayoutManager,
                                           callback: BehaviorCallback)
            = object : EndlessLoadListener(layoutManager) {
        override fun onLoadMore() {
            callback.onPageLoadRequested()
        }
    }

/**
 * A very simple adapter backed by a mutable list that exposes a method to add items.
 * An alternative would have been to use the databinding library, but the fact that it does not
 * support `merge` layouts would make diverse screen support more complicated.
 */
internal class Adapter(private val listener: OnItemClickListener<Post>)
    : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val items = mutableListOf<Post>()
    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(target: RecyclerView) {
        recyclerView = target
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_post, parent, false), recyclerView, listener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(items[position])
        if (position == items.size - 1) {
            holder.addBottomMargin()
        }
    }

    override fun getItemCount(): Int = items.size

    /**
     * This implementation is a bit flawed in theory because it relies on the backing list having
     * unique items, and regardless of the condition being met, there is no reason for it to
     * exist. However, the id returned by the Reddit API is a random string that cannot be safely
     * hashed into a long because the type is bigger (string is 72 bytes, long is 64) so we cannot
     * rely on it.
     */
    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * Requests a list of items to be added to this adapter. This call triggers a UI update.
     * @param toAdd The items to add.
     */
    internal fun addItems(toAdd: List<Post>) {
        val firstInsertedPosition = items.size
        items.addAll(toAdd)
        notifyItemRangeInserted(firstInsertedPosition, toAdd.size)
        (recyclerView.layoutParams as FrameLayout.LayoutParams).bottomMargin = 0
    }

    /**
     * Very simple viewholder that sets text and click event handling.
     * @param itemView The view to dump the held data.
     */
    internal class ViewHolder internal constructor(
            itemView: View,
            private val recyclerView: RecyclerView,
            private val listener: OnItemClickListener<Post>): RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.text_title) as TextView
        private val scoreView: TextView = itemView.findViewById(R.id.text_score) as TextView
        private val subredditView: TextView = itemView.findViewById(R.id.text_subreddit) as TextView

        /**
         * Draw an item.
         * @title The item to draw.
         */
        internal fun render(item: Post) {
            titleView.text = item.title
            scoreView.text = item.score.toString()
            subredditView.text = itemView.context.getString(R.string.subreddit_template,
                    item.subreddit)
            itemView.setOnClickListener { listener.onItemClicked(item) }
        }

        /**
         * Adds a margin under the recycler view for the progress and error views to show.
         */
        internal fun addBottomMargin() {
            (recyclerView.layoutParams as FrameLayout.LayoutParams).bottomMargin =
                itemView.context.getDimension(R.dimen.footer_padding).toInt()
        }
    }
}

/**
 * An interface to transmit click events.
 */
internal interface OnItemClickListener<in T> {
    /**
     * To be called then the view for an item is clicked.
     * @param item The item corresponding to the view clicked.
     */
    fun onItemClicked(item: T)
}

/**
 * @see <a href="https://gist.githubusercontent.com/nesquena/d09dc68ff07e845cc622/raw/e2429b173f75afb408b420ad4088fed68240334c/EndlessRecyclerViewScrollListener.java">Adapted from CodePath</a>
 */
abstract class EndlessLoadListener(
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
