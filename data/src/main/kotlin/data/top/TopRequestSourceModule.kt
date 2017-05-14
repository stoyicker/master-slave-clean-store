package data.top

import com.nytimes.android.external.fs.FileSystemPersister
import com.nytimes.android.external.fs.PathResolver
import com.nytimes.android.external.fs.filesystem.FileSystemFactory
import com.nytimes.android.external.store.base.impl.StoreBuilder
import com.nytimes.android.external.store.middleware.moshi.MoshiParserFactory
import dagger.Component
import dagger.Module
import dagger.Provides
import data.common.ApiService
import data.common.NetworkModule
import okio.BufferedSource
import retrofit2.Retrofit
import util.android.IndexedPersistedByDiskStore
import java.io.File
import javax.inject.Singleton

/**
 * A component to inject instances that require access to data provided by TopRequestSourceModule.
 * @see TopRequestSourceModule
 */
@Component(modules = arrayOf(NetworkModule::class, TopRequestSourceModule::class))
@Singleton
internal interface TopRequestSourceComponent {
    fun inject(target: TopRequestSource)
}

/**
 * Module used to provide stuff required by TopRequestSource objects.
 */
@Module(includes = arrayOf(NetworkModule::class))
internal class TopRequestSourceModule(private val cacheDir: File) {
    @Provides
    @Singleton
    fun cacheDir() = cacheDir

    @Provides
    @Singleton
    fun apiServiceAccessor(retrofit: Retrofit): ApiService
            = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun pageMap() = IndexedPersistedByDiskStore(cacheDir.resolve("pageMap"),
            object : IndexedPersistedByDiskStore.ValueStringifier<String> {
                override fun fromString(source: String) = if (source == "null") null else source

                override fun toString(source: String?) = source ?: "null"
            }, mutableMapOf(0 to null as String?)).also { it.restore() }

    @Provides
    @Singleton
    fun store(apiService: ApiService, pageMap: IndexedPersistedByDiskStore<String>) =
            // We want to have long-term caching, since it is about all-time tops, which do not
            // change very frequently. Therefore we are fine using the default memory cache
            // implementation which expires items in 24h after acquisition.
            // We will also use disk caching to prepare against connectivity-related problems,
            // but we will default to checking the network because on app opening it is
            // reasonable to expected that, if network connectivity available, the data shown
            // should be the latest
            StoreBuilder
                    .parsedWithKey<TopRequestParameters, BufferedSource, TopRequestDataContainer>()
                    .fetcher({ topFetcher(it, apiService, pageMap) })
                    .parser(MoshiParserFactory.createSourceParser<TopRequestDataContainer>(
                            TopRequestDataContainer::class.java))
                    .persister(FileSystemPersister.create(
                            FileSystemFactory.create(cacheDir),
                            PathResolver<TopRequestParameters> { it.toString() }))
                    // Never try to refresh from network on stale since it will very likely not
                    // be worth and it is not required because we do it on app launch anyway
                    .refreshOnStale()
                    .open()

    /**
     * Provides a Fetcher for the store.
     * @param topRequestParameters The parameters for the request.
     * @see com.nytimes.android.external.store.base.Fetcher
     */
    private fun topFetcher(topRequestParameters: TopRequestParameters, apiService: ApiService,
                           pageMap: IndexedPersistedByDiskStore<String>) = apiService
            .top(topRequestParameters.subreddit, topRequestParameters.time.value,
                    if (pageMap.containsKey(topRequestParameters.page))
                        pageMap[topRequestParameters.page]
                    else
                    // Skipping pages is not possible, on attempt just prevent it
                        pageMap[pageMap.keys.last()]
                    , topRequestParameters.limit)
            .map { it.source() }
}
