package app

import android.app.Activity
import android.app.Application
import android.support.v7.widget.RecyclerView
import android.view.View
import app.gaming.TopGamingPostsActivityComponentBuilder
import data.top.TopPostsFacade
import domain.Domain

/**
 * Custom application.
 */
internal open class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Domain.topPostsFacade(TopPostsFacade())
    }

    /**
     * TopGamingAllTimePostsActivity can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.gaming.TopGamingAllTimePostsActivity
     */
    internal fun buildTopGamingPostsActivityComponent(
            contentView: RecyclerView, errorView: View, progressView: View, activity: Activity) =
            TopGamingPostsActivityComponentBuilder.buildComponent(
                    contentView, errorView, progressView, activity)
}
