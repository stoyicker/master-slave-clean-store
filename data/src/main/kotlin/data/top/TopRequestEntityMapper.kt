package data.top

import data.common.DataPost
import domain.entity.Post
import org.jorge.ms.data.BuildConfig

/**
 * Entity mapper between domain and data.
 */
internal class TopRequestEntityMapper {

    /**
     * Maps a data post to a domain post.
     */
    fun transform(dataPost: DataPost) = Post(
            dataPost.id,
            dataPost.title,
            dataPost.subreddit,
            dataPost.score,
            // This is rather ugly but I do not want to pass a Context just to resolve a string
            // template (for usage of Context#getString(int resId, String... args)
            "${BuildConfig.API_URL}${dataPost.permalink.drop(1)}")
}
