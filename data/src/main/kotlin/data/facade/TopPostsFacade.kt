package data.facade

import data.network.top.TopRequestDataContainer
import data.network.top.TopRequestEntityMapper
import data.network.top.TopRequestParameters
import data.network.top.TopRequestSource
import domain.entity.Post
import domain.entity.TimeRange
import domain.repository.DomainTopPostsFacade
import rx.Observable

/**
 * Exposes entry points to top data requests. If we were to have more
 * requests, it would probably be a good idea to have more facades, not necessarily on a 1:1
 * facade-request proportion, but just so we don't end up exposing too many requests in the same
 * facade.
 * Having a Facade might seem like "too much decoupling" (if such a thing exists) since we
 * already have a dedicated data module, but it eases separating unit from integration tests,
 * since the request sources, like TopRequestSource, just default to already unit-tested third-party
 * delegates, and therefore only really need to have integration tests.
 */
object TopPostsFacade : DomainTopPostsFacade {

    /**
     * Fetches top posts in a given subreddit, first trying the network and then falling back to
     * the cache. All caches are updated on success. Use when getting the latest content is more
     * important than a fast and reliable response.
     * @param subreddit The subreddit to query.
     * @param timeRange The time range (one of hour, day, week, month, or all).
     * @param page The page to request.
     */
    override fun fetchTop(subreddit: CharSequence, timeRange: TimeRange, page: Int)
        : Observable<Post> = mapToDomain(
                TopRequestSource.fetch(TopRequestParameters(subreddit, timeRange, page)))

    /**
     * Gets top posts in a given subreddit, first trying the network and then falling back to
     * the cache. Use when a fast and reliable response is more important than obtaining the latest
     * content.
     * @param subreddit The subreddit to query.
     * @param timeRange The time range (one of hour, day, week, month, or all).
     * @param page The page to request.
     */
    override fun getTop(subreddit: CharSequence, timeRange: TimeRange, page: Int)
            : Observable<Post> = mapToDomain(
            TopRequestSource.get(TopRequestParameters(subreddit, timeRange, page)))

    /**
     * Prepares the data in a top response to be consumed by outer modules.
     * @param parsedDataResponse The response as it is made available to this module after parsing.
     */
    private fun mapToDomain(parsedDataResponse: Observable<TopRequestDataContainer>)
        = parsedDataResponse.flatMapIterable {
            it.data.children.map {
                TopRequestEntityMapper.transform(it.data)
            }
        }
}
