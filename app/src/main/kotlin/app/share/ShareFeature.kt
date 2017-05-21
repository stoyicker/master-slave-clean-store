package app.share

import android.content.Context
import android.content.Intent
import org.jorge.ms.app.R

/**
 * Wraps sharing functionality.
 */
internal class ShareFeature(private val context: Context) {
    /**
     * Executes a share action.
     * @param item The time to share.
     */
    fun share(item: ShareFeature.Shareable) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_SUBJECT, item.getShareableTitle())
        intent.putExtra(Intent.EXTRA_TEXT, item.getShareableLink())
        intent.type = "text/plain"
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
    }

    /**
     * An interface to define shareable items.
     */
    interface Shareable {
        /**
         * Implementations should provide a title representative of this item when shared.
         * @return A title representative of this item when shared.
         */
        fun getShareableTitle(): String

        /**
         * Implementations should provide a uri representative of this item when shared.
         * @return A uri representative of this item when shared.
         */
        fun getShareableLink(): String
    }
}
