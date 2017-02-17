package data.network.top

/**
 * This class wraps the parameters for a top-in-subreddit request.
 */
internal data class TopRequestParameters(val subreddit: CharSequence, val time: CharSequence, val page: Int) {
    internal val limit = 25
}
