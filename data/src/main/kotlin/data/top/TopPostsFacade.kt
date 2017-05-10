package data.top

import data.ComponentHolder
import domain.entity.Post
import domain.entity.TimeRange
import domain.repository.DomainTopPostsFacade
import rx.Observable
import javax.inject.Inject

/**
 * Exposes entry points to top data requests. If we were to have more requests, it would probably be
 * a good idea to have more facades, not necessarily on a 1:1 facade-request proportion,
 * but just so we don't end up exposing too many requests in the same facade.
 * Having a facade might seem like "too much decoupling" (if such a thing exists) since we
 * already have a dedicated data module, but it eases separating unit from integration tests,
 * since the request sources, like TopRequestSource, are just a layer of caching that is easily
 * tested built directly on top of third-party dependencies that are trusted to be tested.
 */
class TopPostsFacade : DomainTopPostsFacade {
    @Inject
    internal lateinit var entityMapper: TopRequestEntityMapper
    @Inject
    internal lateinit var source: TopRequestSource

    init {
        ComponentHolder.topPostsFacadeComponent.inject(this)
    }

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
            source.fetch(TopRequestParameters(subreddit, timeRange, page)))

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
            source.get(TopRequestParameters(subreddit, timeRange, page)))

    /**
     * Prepares the data in a top response to be consumed by outer modules.
     * @param parsedDataResponse The response as it is made available to this module after parsing.
     */
    private fun mapToDomain(parsedDataResponse: Observable<TopRequestDataContainer>)
        = parsedDataResponse.flatMapIterable {
            it.data.children.map {
                entityMapper.transform(it.data)
            }
        }
}
