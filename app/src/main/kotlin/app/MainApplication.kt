package app

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentCallbacks2
import android.support.annotation.VisibleForTesting
import data.Data
import data.facade.TopPostsFacade
import domain.Domain
import domain.callback.Urgency

/**
 * Custom application.
 */
internal open class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Domain.inject(TopPostsFacade)
    }

    /**
     * onTrimMemory is not called before API 14, so we need to reroute according to the
     * documentation.
     */
    override final fun onLowMemory() {
        onTrimMemory(TRIM_MEMORY_COMPLETE_VALUE)
    }

    override final fun onTrimMemory(level: Int) {
        val urgency: Urgency
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> return
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> return
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> urgency = Urgency.LOW
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> urgency = Urgency.MEDIUM
            else -> urgency = Urgency.HIGH
        }
        Data.onTrimMemory(urgency)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal companion object {
        /**
         * For compat use, define a const so there is no need to rely on the platform run against
         */
        @SuppressLint("InlinedApi")
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val TRIM_MEMORY_COMPLETE_VALUE = ComponentCallbacks2.TRIM_MEMORY_COMPLETE
    }
}
