package data.network.top

import domain.entity.TimeRange

/**
 * This class wraps the parameters for a top-in-subreddit request.
 */
internal data class TopRequestParameters(
        internal val subreddit: CharSequence,
        internal val time: TimeRange,
        internal val page: Int) {
    internal val limit = 25
}
