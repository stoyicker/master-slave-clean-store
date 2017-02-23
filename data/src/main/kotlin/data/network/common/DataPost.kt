package data.network.common

import com.google.gson.annotations.SerializedName

/**
 * Models the relevant information about a post. Note that it is located here because it is not
 * specific to top requests.
 */
internal data class DataPost(@SerializedName("title") val title: String,
                             @SerializedName("subreddit") val subreddit: String,
                             @SerializedName("score") val score: Int,
                             @SerializedName("permalink") val permalink: String)
