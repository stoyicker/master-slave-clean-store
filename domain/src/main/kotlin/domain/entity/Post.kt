package domain.entity

/**
 * Models the relevant information about a post, but in a way that modules other than data can
 * see it without knowing about how it is retrieved (deserialized).
 */
data class Post(
        val id: String,
        val title: String,
        val subreddit: String,
        val score: Int,
        val thumbnailLink: String?,
        val url: String)
