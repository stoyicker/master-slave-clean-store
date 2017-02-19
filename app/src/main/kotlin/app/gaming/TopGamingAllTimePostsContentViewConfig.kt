package app.gaming

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import app.ext.getDimension
import app.ext.getInteger
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
    internal fun dumpOnto(recyclerView: RecyclerView) {
        recyclerView.adapter = provideAdapter()
        recyclerView.itemAnimator = provideItemAnimator()
        recyclerView.layoutManager = provideLayoutManager(recyclerView.context)
        recyclerView.setHasFixedSize(false)
    }

    /**
     * Provides an adapter with stable ids.
     */
    private fun provideAdapter(): RecyclerView.Adapter<out RecyclerView.ViewHolder> {
        val adapter = Adapter()
        adapter.setHasStableIds(true)
        return adapter
    }

    /**
     * Provides an alpha-based item animator.
     */
    private fun provideItemAnimator() = ItemAnimator()

    /**
     * Provides a layout manager based on the size of the screen.
     * @param context The context
     */
    private fun provideLayoutManager(context: Context) = LinearLayoutManager(context)
}

/**
 * A very simple adapter backed by a mutable list that exposes a method to add items.
 * An alternative would have been to use the databinding library, but the fact that it does not
 * support `merge` layouts would make diverse screen support more complicated.
 */
internal class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val items = mutableListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_post, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(items[position])
        if (position == items.size - 1) {
            holder.addLastItemMargin()
        } else {
            holder.clearMargin()
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
    }

    /**
     * Very simple viewholder.
     * @param itemView The view to dump the held data.
     */
    internal class ViewHolder internal constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.text_title) as TextView
        private val scoreView: TextView = itemView.findViewById(R.id.text_score) as TextView
        private val subredditView: TextView = itemView.findViewById(R.id.text_subreddit) as TextView

        /**
         * Draw an item.
         * @title The item to draw.
         */
        internal fun render(item: Post) {
            DEFAULT_BOTTOM_MARGIN = (itemView.layoutParams as RecyclerView.LayoutParams).bottomMargin
            titleView.text = item.title
            scoreView.text = item.score.toString()
            subredditView.text = itemView.context.getString(R.string.subreddit_template, item.subreddit)
        }

        /**
         * Clears the margin added for the footer.
         */
        internal fun clearMargin() {
            (itemView.layoutParams as RecyclerView.LayoutParams).bottomMargin = 0
        }

        /**
         * Not very proud of this, but a real footer with a custom decoration would have taken
         * extra time.
         */
        internal fun addLastItemMargin() {
            (itemView.layoutParams as RecyclerView.LayoutParams).bottomMargin +=
                itemView.context.getDimension(R.dimen.footer_padding).toInt()
        }

        companion object {
            private var DEFAULT_BOTTOM_MARGIN: Int = 0
        }
    }
}

/**
 * A simple item animator for this recycler view. There will be no removals, so there is no need
 * to specify an animation for them either.
 */
private class ItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder != null) {
            ViewCompat.setAlpha(holder.itemView, 0F)
            ViewCompat.animate(holder.itemView)
                    .alphaBy(1F)
                    .setInterpolator(DecelerateInterpolator())
                    .setDuration(holder.itemView.context
                            .getInteger(android.R.integer.config_longAnimTime).toLong())
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            dispatchAddStarting(holder)
                        }

                        override fun onAnimationEnd(view: View) {
                            dispatchAddFinished(holder)
                        }

                        override fun onAnimationCancel(view: View) {}
                    })
            return true
        }
        return false
    }
}

