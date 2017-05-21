package data.common

import domain.entity.Post

/**
 * Entity mapper between domain and data.
 */
internal class DomainEntityMapper {
    /**
     * Maps a data post to a domain post.
     */
    fun transform(dataPost: DataPost) = Post(
            dataPost.id,
            dataPost.title,
            dataPost.subreddit,
            dataPost.score,
            dataPost.thumbnailLink,
            dataPost.url)
}
