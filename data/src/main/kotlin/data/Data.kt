package data

import android.os.Environment
import android.support.annotation.VisibleForTesting
import data.network.top.TopRequestSource
import domain.callback.MemoryCallbacks
import domain.callback.Urgency

/**
 * Global configuration holder for the module.
 * Note how this class acts as a dependency holder. You could also a DI framework like Dagger for
 * example, but to only provide a single dependency, which is also a singleton, might as well do it
 * myself instead.
 */
object Data : MemoryCallbacks {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal const val PAGE_KEPT_ON_MEMORY_TRIM_LOW = 5
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal const val PAGE_KEPT_ON_MEMORY_TRIM_MEDIUM = 1
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal const val PAGE_KEPT_ON_MEMORY_TRIM_HIGH = 0
    internal val cacheDir by lazy { Inject.cacheDirGenerator.invoke() }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var cacheablePagedSources: Array<CacheablePagedSource> = arrayOf(TopRequestSource)

    override fun onTrimMemory(urgency: Urgency) {
        val page: Int
        when (urgency) {
            Urgency.LOW -> page = PAGE_KEPT_ON_MEMORY_TRIM_LOW
            Urgency.MEDIUM -> page = PAGE_KEPT_ON_MEMORY_TRIM_MEDIUM
            Urgency.HIGH -> page = PAGE_KEPT_ON_MEMORY_TRIM_HIGH
        }
        cacheablePagedSources.forEach { it.clearCacheFromPage(page) }
    }

     internal object Inject {
         /**
          * Set a cache dir. The way to do it is by providing a generator function that will be
          * invoked the first time the field is accessed.
          */
         var cacheDirGenerator = DEFAULTS.CACHE_DIR_GENERATOR
    }

    private object DEFAULTS {
        internal val CACHE_DIR_GENERATOR = { Environment.getExternalStorageDirectory() }
    }
}

/**
 * Interface to allow mocking sources for injection since they are singletons implemented as object,
 * which cannot be open nor therefore mocked.
 */
internal interface CacheablePagedSource {
    fun clearCacheFromPage(page: Int)
}
