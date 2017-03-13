package domain.repository

import domain.entity.Post
import domain.entity.TimeRange
import rx.Observable

/**
 * This describes all this module needs to know about our DataFacade.
 */
interface DomainTopPostsFacade {
    /**
     * Fetch top posts from a subreddit.
     * @param subreddit The subreddit.
     * @param timeRange The time range.
     * @param page The page.
     */
    fun fetchTop(subreddit: CharSequence, timeRange: TimeRange, page: Int): Observable<Post>
    /**
     * Get top posts from a subreddit.
     * @param subreddit The subreddit.
     * @param timeRange The time range.
     * @param page The page.
     */
    fun getTop(subreddit: CharSequence, timeRange: TimeRange, page: Int): Observable<Post>
}
