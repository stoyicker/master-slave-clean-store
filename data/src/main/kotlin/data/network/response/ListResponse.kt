package data.network.response

import com.google.gson.annotations.SerializedName
import data.network.model.DataModel

/**
 * Models the relevant part of the response to a list request.
 */
data class ListResponse(@SerializedName("recipes") val dataModels: List<DataModel>)
