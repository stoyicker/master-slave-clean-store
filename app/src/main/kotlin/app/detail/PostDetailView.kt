package app.detail

import android.widget.ImageView
import android.widget.TextView
import app.common.PresentationPost

/**
 * Wraps UI behavior for top all time gaming posts scenario. Class is only open for testing
 * purposes.
 */
internal open class PostDetailView(
        private val textView: TextView,
        private val imageView: ImageView) : TextAndImageView<PresentationPost> {
    override fun updateContent(item: PresentationPost) {
        textView.text = item.title
        // TODO Update image view
    }
}
