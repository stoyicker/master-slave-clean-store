package data.top

import com.squareup.moshi.Json
import data.common.DataPost

/**
 * Models the relevant information about information that comes in a container with a type (kind)
 * and payload (data).
 */
internal data class TopRequestDataContainer(@Json(name = "data") val data: TopRequestData)

/**
 * Models the relevant information about a top request data.
 */
internal data class TopRequestData(@Json(name = "children") val children: List<DataPostContainer>,
                                   @Json(name = "after") val after: String?)

/**
 * Wraps posts.
 */
internal data class DataPostContainer(@Json(name = "data") val data: DataPost)
