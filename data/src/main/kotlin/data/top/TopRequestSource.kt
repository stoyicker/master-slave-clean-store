package data.top

import com.nytimes.android.external.store3.base.impl.Store
import data.ComponentHolder
import io.reactivex.Single
import util.android.IndexedPersistedByDiskStore
import javax.inject.Inject
import dagger.Lazy as DaggerLazy

/**
 * Contains the data source for top requests.
 */
internal class TopRequestSource {
    // Dagger does not play well with providing Kotlin's Lazy instances, so we do a workaround by
    // targeting a holder from Dagger's Lazy and then converting it to Kotlin's.
    // Copying the actual value to a different reference holder instead of requesting it through the
    // accessor every time trades off a slightly higher on-paper peak RAM consumption to minimize
    // virtual method usage.
    // See https://developer.android.com/training/articles/perf-tips.html#GettersSetters for a
    // similar use case with internal getters and setters.
    @Inject
    lateinit var pageMapAccessor: DaggerLazy<IndexedPersistedByDiskStore<String>>
    val pageMap: IndexedPersistedByDiskStore<String> by lazy { pageMapAccessor.get() }
    @Inject
    lateinit var storeAccessor: DaggerLazy<Store<TopRequestDataContainer, TopRequestParameters>>
    val store: Store<TopRequestDataContainer, TopRequestParameters> by lazy { storeAccessor.get() }

    init {
        ComponentHolder.topRequestSourceComponent.inject(this)
    }

    /**
     * Delegates to its internal responsible for the request. Cache is ignored, but updated on
     * success. On failure, cache is the fallback.
     * @param topRequestParameters The parameters of the request.
     * @see Store
     */
    internal fun fetch(topRequestParameters: TopRequestParameters) =
            if (pageMap[topRequestParameters.page] != NO_MORE_PAGES) {
                updatePageMapAndContinue(topRequestParameters.page, store.fetch(
                        topRequestParameters)
                        .onErrorResumeNext { store.get(topRequestParameters) })
            } else {
                Single.just(TopRequestDataContainer.EMPTY)
            }

    /**
     * Delegates to its internal responsible for the request. Cache checks: memory > disk > network.
     * @param topRequestParameters The parameters of the request.
     * @see Store
     */
    internal fun get(topRequestParameters: TopRequestParameters) =
            if (pageMap[topRequestParameters.page] != NO_MORE_PAGES) {
                updatePageMapAndContinue(topRequestParameters.page, store.get(topRequestParameters))
            } else {
                Single.just(TopRequestDataContainer.EMPTY)
            }

    /**
     * Update the pagination representation: The doOnNext allows us to intercept the interpretation
     * of pagination of the API so that the outer world only needs to know what page it wants, not
     * how the API implements pagination.
     * @param requestPage The page requested.
     * @param from An observable of the desired data.
     */
    private fun updatePageMapAndContinue(requestPage: Int,
                                         from: Single<TopRequestDataContainer>) =
            from.doOnSuccess { it.data.after.let {
                pageMap.put(requestPage + 1, it ?: NO_MORE_PAGES)
            }
    }

    private companion object {
        private const val NO_MORE_PAGES = "NO_MORE_PAGES"
    }
}
