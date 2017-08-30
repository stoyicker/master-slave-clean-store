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
            id = post.id,
            title = HtmlCompat.fromHtml(post.title).toString(),
            subreddit = post.subreddit,
            score = post.score,
            thumbnailLink = post.thumbnailLink,
            url = post.url)
}
