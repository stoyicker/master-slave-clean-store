package app

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import app.gaming.DaggerTopGamingAllTimePostsFeatureInstrumentationComponent
import app.gaming.TopGamingAllTimePostsFeatureInstrumentationModule

/**
 * Custom application.
 */
internal open class AndroidTestApplication : MainApplication() {
    /**
     * TopGamingAllTimePostsActivity can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.gaming.TopGamingAllTimePostsActivity
     */
    override fun buildTopGamingAllTimePostsFeatureComponent(
            contentView: RecyclerView, errorView: View, progressView: View, guideView: View,
            activity: Activity) =
            DaggerTopGamingAllTimePostsFeatureInstrumentationComponent.builder()
                    .topGamingAllTimePostsFeatureInstrumentationModule(
                            TopGamingAllTimePostsFeatureInstrumentationModule(
                                    contentView, errorView, progressView, guideView, activity))
                    .build()
}
