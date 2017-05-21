package app.common

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import app.share.ShareFeature

/**
 * Altered model for presentation purposes.
 */
@paperparcel.PaperParcel
internal data class PresentationPost(
        val id: String,
        val title: String,
        val subreddit: String,
        val score: Int,
        val thumbnailLink: String,
        val url: String) : Parcelable, ShareFeature.Shareable {
    override fun hashCode() = id.hashCode()

    override fun equals(other: Any?) = other is PresentationPost && id == other.id

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPresentationPost.writeToParcel(this, dest, flags)
    }

    override fun getShareableTitle() = title

    override fun getShareableLink(): Uri = Uri.parse(url)

    companion object {
        @Suppress("unused") // Parcelable
        @JvmField val CREATOR = PaperParcelPresentationPost.CREATOR
    }
}
