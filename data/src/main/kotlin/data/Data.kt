package data

import android.support.annotation.VisibleForTesting
import data.network.top.TopRequestSource
import domain.callback.MemoryCallbacks
import domain.callback.Urgency
import java.io.File

/**
 *  Holder for the module.
 */
object Data : MemoryCallbacks {
    internal var cacheDir: File? = null
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal const val PAGE_KEPT_ON_MEMORY_TRIM_LOW = 5
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal const val PAGE_KEPT_ON_MEMORY_TRIM_MEDIUM = 1
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal const val PAGE_KEPT_ON_MEMORY_TRIM_HIGH = 0
}

/**
 * Interface to allow mocking sources for injection since they are singletons implemented as object,
 * which cannot be open nor therefore mocked.
 */
internal interface CacheablePagedSource {
    fun clearCacheFromPage(page: Int)
}
