package app

import android.app.Application
import data.TopPostsFacade
import domain.Domain

/**
 * Custom application.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Domain.inject(TopPostsFacade)
    }
}
