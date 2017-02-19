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
            // This is rather ugly but I do not want to pass a Context to the module just ro
            // resolve a string template (for usage of Context#getString(int resId, String... args)
            "${BuildConfig.API_URL}${dataPost.permalink.drop(1)}")
}
