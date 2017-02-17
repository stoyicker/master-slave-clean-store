package data

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
     * Gets top posts in a given subreddit, first trying the cache and then falling back to
     * the network. Use when fast response is more important that getting the latest content.
     * @param subreddit The subreddit to fetch.
     * @param timeRange The time range (one of hour, day, week, month, or all).
     * @param page The page to request.
     */
    override fun getTop(subreddit: CharSequence, timeRange: TimeRange, page: Int)
        : Observable<Post> = sanitizeTopResponse(
                TopRequestSource.get(TopRequestParameters(subreddit, timeRange.value, page)))

    /**
     * Prepares the data in a top response to be consumed by outer modules.
     * @param parsedDataResponse The response as it is made available to this module after parsing.
     */
    private fun sanitizeTopResponse(parsedDataResponse: Observable<TopRequestDataContainer>)
        = parsedDataResponse.flatMapIterable {
            it.data.children.map {
                TopRequestEntityMapper.transform(it.data)
            }
        }
}
