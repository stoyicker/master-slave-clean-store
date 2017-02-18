package data.network.top

import data.network.common.DataPost
import domain.entity.Post
import org.jorge.ms.data.BuildConfig

/**
 * Entity mapper between domain and data.
 */
object TopRequestEntityMapper {

    /**
     * Maps a data post to a domain post.
     */
    internal fun transform(dataPost: DataPost) = Post(
            dataPost.title,
            dataPost.subreddit,
            dataPost.score,
            "${BuildConfig.API_URL}/${dataPost.permalink}")
}
