package data.common

import com.squareup.moshi.Json

/**
 * Models the relevant information about a post. Note that it is located here because it is not
 * specific to top requests.
 */
internal data class DataPost(
        @Json(name = "id") val id: String,
        @Json(name = "title") val title: String,
        @Json(name = "subreddit_name_prefixed") val subreddit: String,
        @Json(name = "score") val score: Int,
        @Json(name = "permalink") val permalink: String) {
    override fun hashCode() = id.hashCode()

    override fun equals(other: Any?) = other is DataPost && id == other.id
}
