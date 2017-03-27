package app.gaming

import android.support.v7.widget.RecyclerView
import android.view.View
import app.widget.LoadableContentView
import domain.entity.Post

/**
 * Wraps UI behavior for top all time gaming posts scenario.
 */
internal class TopGamingAllTimePostsView(
       internal val contentView: RecyclerView,
       internal val errorView: View,
       private val progressView: View) : LoadableContentView<Post> {
    override fun showLoadingLayout() {
        progressView.visibility = View.VISIBLE
    }

    override fun hideLoadingLayout() {
        progressView.visibility = View.GONE
    }

    override fun updateContent(actionResult: List<Post>) {
        (contentView.adapter as Adapter).addItems(actionResult)
    }

    override fun showErrorLayout() {
        errorView.visibility = View.VISIBLE
    }

    override fun hideErrorLayout() {
        errorView.visibility = View.GONE
    }
}
