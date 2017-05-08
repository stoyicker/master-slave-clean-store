package app.gaming

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * A delegate to build the relevant component. Its implementation can be replaced during test
 * runs to provide different dependencies as required by tests.
 */
internal object TopGamingPostsActivityComponentBuilder {
    internal fun buildComponent(
            contentView: RecyclerView, errorView: View, progressView: View, activity: Activity) =
            DaggerTopGamingPostsActivityComponent.builder()
                    .topGamingPostsActivityModule(
                            TopGamingPostsActivityModule(
                                    contentView, errorView, progressView, activity))
                    .build()
}
