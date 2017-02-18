package app.gaming

import android.view.View
import app.LoadableContentView
import domain.entity.Post

/**
 * Wraps UI behavior for top all time gaming posts scenario.
 */
data class TopGamingAllTimePostsView(
       private val contentView: View, private val errorView: View, private val progressView: View)
    : LoadableContentView<Post> {
    override fun showLoadingLayout() {
    }

    override fun hideLoadingLayout() {
    }

    override fun showContentLayout(actionResult: List<Post>) {
    }

    override fun showErrorLayout(throwable: Throwable?) {
    }

    override fun hideErrorLayout() {
    }
}
