package app.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import app.common.PresentationPost
import com.squareup.picasso.Picasso

/**
 * Wraps UI behavior for top all time gaming posts scenario. Class is only open for testing
 * purposes.
 */
internal open class PostDetailView(
        private val textView: TextView,
        private val imageView: ImageView) : TextAndImageView<PresentationPost> {
    override fun updateContent(item: PresentationPost) {
        item.title.let {
            textView.text = it
            imageView.contentDescription = it
        }
        Picasso.with(imageView.context)
                .load(item.thumbnailLink)
                // Trick for correct image placement
                .placeholder(ColorDrawable(Color.TRANSPARENT))
                .fit()
                .centerInside()
                .into(imageView)
    }
}
