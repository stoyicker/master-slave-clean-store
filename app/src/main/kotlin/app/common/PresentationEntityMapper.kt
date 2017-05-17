package app.common

import domain.entity.Post

/**
 * Entity mapper between presentation and domain.
 */
internal class PresentationEntityMapper {
    /**
     * Maps a domain post to a presentation post.
     */
    fun transform(post: Post) = PresentationPost(
            post.id,
            post.title,
            post.subreddit,
            post.score,
            post.detailLink,
            post.thumbnailLink)
}
