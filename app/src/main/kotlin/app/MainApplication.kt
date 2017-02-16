package app

import android.app.Application
import data.DataProvider

/**
 * Custom application.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataProvider.init(cacheDir)
    }
}
