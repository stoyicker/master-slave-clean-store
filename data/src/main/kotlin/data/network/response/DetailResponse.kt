package data.network.response

import com.google.gson.annotations.SerializedName
import data.network.model.DataModel

/**
 * Models the relevant part of the response to a detail request.
 */
data class DetailResponse(@SerializedName("recipe") val dataModel: DataModel)
