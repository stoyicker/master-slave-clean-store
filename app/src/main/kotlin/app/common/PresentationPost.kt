package app.common

import android.os.Parcel
import android.os.Parcelable

/**
 * Altered model for presentation purposes.
 */
@paperparcel.PaperParcel
internal data class PresentationPost(
        val id: String,
        val title: String,
        val subreddit: String,
        val score: Int,
        val detailLink: String,
        val thumbnailLink: String) : Parcelable {
    override fun hashCode() = id.hashCode()

    override fun equals(other: Any?) = other is PresentationPost && id == other.id

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPresentationPost.writeToParcel(this, dest, flags)
    }

    companion object {
        @Suppress("unused") // Parcelable
        @JvmField val CREATOR = PaperParcelPresentationPost.CREATOR
    }
}
