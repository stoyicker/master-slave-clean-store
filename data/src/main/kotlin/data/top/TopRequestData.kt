package data.top

import com.google.gson.annotations.SerializedName
import data.common.DataPost

/**
 * Models the relevant information about information that comes in a container with a type (kind)
 * and payload (data).
 * Actually, this could be used for the whole Reddit API since there are probably more requests
 * that used this "contained" format. Unfortunately, using generics to support that approach
 * would not be possible because type erasure would cause gson to fail to deserialize correctly
 * and inheritance is not possible either because data classes cannot be open. That is why
 * this class sits in the 'data.network.top' package specifically instead of just in 'data.network'.
 */
internal data class TopRequestDataContainer(@SerializedName("data") val data: TopRequestData)

/**
 * Wraps posts.
 */
internal data class TopDataPostContainer(@SerializedName("data") val data: DataPost)

/**
 * Models the relevant information about a top request data.
 */
internal data class TopRequestData(@SerializedName("children") val children: List<TopDataPostContainer>,
                                   @SerializedName("after") val after: String)
