package data

import com.nytimes.android.external.cache.CacheBuilder
import com.nytimes.android.external.fs.SourcePersisterFactory
import com.nytimes.android.external.store.base.Fetcher
import com.nytimes.android.external.store.base.impl.BarCode
import com.nytimes.android.external.store.base.impl.Store
import com.nytimes.android.external.store.base.impl.StoreBuilder
import com.nytimes.android.external.store.middleware.GsonParserFactory
import data.network.ApiService
import data.network.response.DetailResponse
import data.network.response.ListResponse
import okio.BufferedSource
import org.jorge.ms.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Observable
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Exposes entry points to data obtained by and handled in this module.
 */
object DataProvider {
    private const val CACHE_EXPIRES_IN_MINUTES = 10L
    private const val KEY_TYPE_DETAIL = "DETAIL"
    private val retrofit: ApiService = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .validateEagerly(true)
            .build()
            .create(ApiService::class.java)
    private lateinit var listStore: Store<ListResponse, Int>
    private lateinit var detailStore: Store<DetailResponse, BarCode>

    /**
     * Initializes the stores.
     */
    fun init(cacheDir: File) {
        // We do not want to have long-term caching for the list, since it is 'always-latest'
        // rather than key-indexed, so use no persister
        listStore = StoreBuilder.parsedWithKey<Int, BufferedSource, ListResponse>()
                // The default configuration would keep this request considered non-stale for 24h,
                // but that is not good for the list case since we only want cached records on
                // rotation and resume-from-background cases, and we do not need 24h for that
                .memory(CacheBuilder.newBuilder()
                        .expireAfterWrite(CACHE_EXPIRES_IN_MINUTES, TimeUnit.MINUTES)
                        .build<Int, Observable<ListResponse>>())
                .fetcher({ this.listFetcher(it) })
                .parser(GsonParserFactory.createSourceParser<ListResponse>(ListResponse::class.java))
                // Always try to refresh from network on stale since this is likely to change
                .networkBeforeStale()
                .open()
        detailStore = StoreBuilder.parsedWithKey<BarCode, BufferedSource, DetailResponse>()
                .fetcher({ this.detailFetcher(it) })
                .parser(GsonParserFactory.createSourceParser<DetailResponse>(DetailResponse::class.java))
                // We do want long-term caching for the detail as it is indexed, so use a persister
                .persister(this.persister(cacheDir))
                // Just refresh details when not forced to re-download, since they are not likely to change
                .refreshOnStale()
                .open()
    }

    /**
     * Gets the list of models in a given page, first trying the network and updating the cache if
     * successful, or falling back to the cache if unsuccessful.
     * @param page the page to fetch.
     */
    fun fetchList(page: Int) = listStore.get(page)

    /**
     * Gets the list of models in a given page, first trying the cache and then falling back to
     * the network. Use when fast response is more important that getting the latest content.
     * @param page The page to fetch.
     */
    fun getList(page: Int) = listStore.get(page)

    /**
     * Gets the detail for an entry, looking first at the cache.
     * @param rId The key of the entry to look for.
     */
    fun getDetail(rId: String) = detailStore.get(BarCode(KEY_TYPE_DETAIL, rId))
            .first()
            .map { it.dataModel }

    /**
     * Gets the detail for an entry, trying first the network and updating the cache if reachable,
     * or falling back to it if unavailable.
     * @param rId The key of the entry to look for.
     */
    fun fetchDetail(rId: String) = detailStore.fetch(BarCode(KEY_TYPE_DETAIL, rId))
            .first()
            .map { it.dataModel }

    /**
     * Provides a Fetcher for the list store.
     * @param page The page to fetch.
     * @see Fetcher
     */
    private fun listFetcher(page: Int)
            = retrofit.list(page, BuildConfig.API_KEY).map { it.source() }

    /**
     * Provides a Fetcher for the detail store.
     * @param idWrapper The identifier of the model to fetch.
     * @see Fetcher
     */
    private fun detailFetcher(idWrapper: BarCode)
            = retrofit.detail(idWrapper.key, BuildConfig.API_KEY).map { it.source() }

    /**
     * Provides a Persister that to cache data in.
     * @see com.nytimes.android.external.fs.SourcePersister
     */
    private fun persister(cacheDir: File) = SourcePersisterFactory.create(cacheDir)
}
