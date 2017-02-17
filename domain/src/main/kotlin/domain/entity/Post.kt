package domain.entity

/**
 * Models the relevant information about a post, but in a way that modules other than data can
 * see it without knowing about how it is retrieved (this is, depending on gson for
 * @SerializedName).
 */
data class Post(val title: String, val subreddit: String, val score: Int, val detailLink: String)
