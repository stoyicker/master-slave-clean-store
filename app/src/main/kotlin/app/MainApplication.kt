package app

import android.app.Application
import data.facade.TopPostsFacade
import domain.Domain

/**
 * Custom application.
 */
internal open class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Domain.topPostsFacade(TopPostsFacade)
    }
}
