package app.common

import domain.entity.Post
import util.android.HtmlCompat

/**
 * Entity mapper between presentation and domain.
 */
internal class PresentationEntityMapper {
    /**
     * Maps a domain post to a presentation post.
     */
    fun transform(post: Post) = PresentationPost(
            post.id,
            HtmlCompat.fromHtml(post.title).toString(),
            post.subreddit,
            post.score,
            post.thumbnailLink,
            post.url)
}
