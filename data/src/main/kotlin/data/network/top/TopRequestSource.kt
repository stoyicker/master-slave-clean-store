package data.network.top

import android.support.annotation.VisibleForTesting
import com.nytimes.android.external.fs.FileSystemPersister
import com.nytimes.android.external.fs.PathResolver
import com.nytimes.android.external.fs.filesystem.FileSystemFactory
import com.nytimes.android.external.store.base.impl.Store
import com.nytimes.android.external.store.base.impl.StoreBuilder
import com.nytimes.android.external.store.middleware.GsonParserFactory
import data.CacheablePagedSource
import data.Data
import data.network.common.ApiService
import data.network.common.RxApiClient
import data.network.top.TopRequestSource.delegate
import data.network.top.TopRequestSource.pageMap
import domain.interactor.TopGamingAllTimePostsUseCase
import okio.BufferedSource
import rx.Observable

/**
 * Contains the data source for top requests.
 */
internal object TopRequestSource : CacheablePagedSource {
    private val apiService by lazy { RxApiClient.retrofit.create(ApiService::class.java) }
    // This wraps the implementation of pagination in the API
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val pageMap = mutableMapOf(0 to "")
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val delegate by lazy {
        // We want to have long-term caching, since it is about all-time tops, which do not
        // change very frequently. Therefore we are fine using the default memory cache
        // implementation which expires items in 24h after acquisition.
        // We will also use disk caching to prepare against connectivity-related problems, but
        // we will default to checking the network because on app opening it is reasonable to
        // expected that, if network connectivity available, the data shown should be the latest
        StoreBuilder.parsedWithKey<TopRequestParameters, BufferedSource, TopRequestDataContainer>()
            .fetcher({ this.topFetcher(it) })
            .parser(GsonParserFactory.createSourceParser<TopRequestDataContainer>(
                    TopRequestDataContainer::class.java))
            .persister(FileSystemPersister.create(
                    FileSystemFactory.create(Data.cacheDir!!),
                    PathResolver<TopRequestParameters> { key -> key.toString() }))
            // Never try to refresh from network on stale since it will very likely not be worth
            // and it is not required because we do it on app launch anyway
            .refreshOnStale()
            .open()
    }

    /**
     * Delegates to its internal responsible for the request. Cache is ignored, but updated on
     * success.
     * @param topRequestParameters The parameters of the request.
     * @see Store
     */
    internal fun fetch(topRequestParameters: TopRequestParameters) =
            updatePageMapAndContinue(topRequestParameters.page,
                    delegate.fetch(topRequestParameters))

    /**
     * Delegates to its internal responsible for the request. Cache checks: memory > disk > network.
     * @param topRequestParameters The parameters of the request.
     * @see Store
     */
    internal fun get(topRequestParameters: TopRequestParameters) =
            updatePageMapAndContinue(topRequestParameters.page, delegate.get(topRequestParameters))

    /**
     * Clears cached entries starting from a given page.
     * @param page The page to start from (inclusive).
     */
    override fun clearCacheFromPage(page: Int) {
        val safePage = Math.max(0, page)
        while (pageMap.size > safePage) {
            pageMap.remove(pageMap.size - 1)
            delegate.clear(TopRequestParameters(
                    TopGamingAllTimePostsUseCase.SUBREDDIT,
                    TopGamingAllTimePostsUseCase.TIME_RANGE,
                    0))
        }
    }

    /**
     * Provides a Fetcher for the top store.
     * @param topRequestParameters The parameters for the request.
     * @see com.nytimes.android.external.store.base.Fetcher
     */
    private fun topFetcher(topRequestParameters: TopRequestParameters) = apiService
            .top(topRequestParameters.subreddit, topRequestParameters.time.value,
                    if (pageMap.containsKey(topRequestParameters.page))
                        pageMap[topRequestParameters.page]
                    else
                        // Skipping pages is not possible, on attempt just prevent it
                        pageMap[pageMap.keys.last()]
                    , topRequestParameters.limit)
            .map { it.source() }

    /**
     * Update the pagination representation: The doOnNext allows us to intercept the interpretation
     * of pagination of the API so that the outer world only needs to know what page it wants, not
     * how the API implements pagination.
     * @param requestPage The page requested.
     * @param from An observable of the desired data.
     */
    private fun updatePageMapAndContinue(requestPage: Int,
                                         from: Observable<TopRequestDataContainer>)
            = from.doOnNext { pageMap.put(requestPage + 1, it.data.after) }
}
