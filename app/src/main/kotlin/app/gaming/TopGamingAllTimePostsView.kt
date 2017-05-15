package app.gaming

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import app.LoadableContentView
import domain.entity.Post
import org.jorge.ms.app.R
import util.android.ext.getDimension

/**
 * Wraps UI behavior for top all time gaming posts scenario.
 */
internal class TopGamingAllTimePostsView(
       internal val contentView: RecyclerView,
       internal val errorView: View,
       private val progressView: View,
       private val guideView: View) : LoadableContentView<Post> {
    override fun showLoadingLayout() {
        pushInfoArea()
        progressView.visibility = View.VISIBLE
        guideView.visibility = View.INVISIBLE
    }

    override fun hideLoadingLayout() {
        progressView.visibility = View.GONE
    }

    override fun updateContent(actionResult: List<Post>) {
        (contentView.adapter as Adapter).addItems(actionResult)
        guideView.visibility = View.VISIBLE
    }

    override fun showErrorLayout() {
        pushInfoArea()
        errorView.visibility = View.VISIBLE
        guideView.visibility = View.INVISIBLE
    }

    override fun hideErrorLayout() {
        errorView.visibility = View.GONE
    }

    private fun pushInfoArea() {
        (contentView.layoutParams as FrameLayout.LayoutParams).bottomMargin =
                contentView.context.getDimension(R.dimen.footer_padding).toInt()
    }
}
