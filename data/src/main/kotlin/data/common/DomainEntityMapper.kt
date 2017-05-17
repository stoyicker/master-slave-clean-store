package data.common

import domain.entity.Post
import org.jorge.ms.data.BuildConfig

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
            "${BuildConfig.API_URL}${dataPost.permalink.drop(1)}",
            dataPost.thumbnailLink)
}
