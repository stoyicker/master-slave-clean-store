package data.network.top

/**
 * This class wraps the parameters for a top-in-subreddit request.
 */
internal data class TopRequestParameters(val subreddit: CharSequence, val time: CharSequence, val after: CharSequence?, val limit: Int)
