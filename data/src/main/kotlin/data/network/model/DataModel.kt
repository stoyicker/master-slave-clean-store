package data.network.model

import com.google.gson.annotations.SerializedName

// TODO Make this internal instead and have an entity mapper run when retrieving an item to convert it to domain, and then change how the builder is created (from DataModel to Model) so it does not expose the internal class DataModel
data class DataModel(@SerializedName("recipe_id") val modelId: String) {
    @SerializedName("publisher")
    lateinit var publisher: String
        private set
    @SerializedName("f2f_url")
    lateinit var f2fUrl: String
        private set
    @SerializedName("ingredients")
    lateinit var ingredients: String
        private set
    @SerializedName("source_url")
    lateinit var sourceUrl: String
        private set
    @SerializedName("image_url")
    lateinit var imageUrl: String
        private set
    @SerializedName("social_rank")
    var socialRank: Float = 0F
        private set
    @SerializedName("publisher_url")
    lateinit var publisherUrl: String
        private set
    @SerializedName("title")
    lateinit var title: String
        private set
}
