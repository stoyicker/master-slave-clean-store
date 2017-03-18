package data

import android.os.Environment

/**
 * Global configuration holder for the module.
 * Note how this class acts as a dependency holder. You could also a DI framework like Dagger for
 * example, but to only provide a single dependency, which is also a singleton, might as well do it
 * myself instead.
 */
object Data {
    internal val cacheDir by lazy { Provide.cacheDirGenerator() }

    internal object Provide {
        /**
         * Set a cache dir. The way to do it is by providing a generator function that will be
         * invoked the first time the field is accessed.
         */
        var cacheDirGenerator = DEFAULTS.CACHE_DIR_GENERATOR
        private object DEFAULTS {
            internal val CACHE_DIR_GENERATOR = { Environment.getExternalStorageDirectory() }
        }
    }
}

/**
 * Interface to allow mocking sources for injection since they are singletons implemented as object,
 * which cannot be open nor therefore mocked.
 */
internal interface CacheablePagedSource {
    fun clearCacheFromPage(page: Int)
}
