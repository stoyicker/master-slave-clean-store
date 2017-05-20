package app

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.detail.DaggerPostDetailFeatureInstrumentationComponent
import app.detail.PostDetailFeatureComponent
import app.detail.PostDetailFeatureInstrumentationModule
import app.gaming.DaggerTopGamingAllTimePostsFeatureInstrumentationComponent
import app.gaming.TopGamingAllTimePostsFeatureInstrumentationComponent
import app.gaming.TopGamingAllTimePostsFeatureInstrumentationModule

/**
 * Custom application.
 */
internal open class AndroidTestApplication : MainApplication() {
    override fun buildTopGamingAllTimePostsFeatureComponent(
            contentView: RecyclerView, errorView: View, progressView: View, guideView: View)
            : TopGamingAllTimePostsFeatureInstrumentationComponent =
            DaggerTopGamingAllTimePostsFeatureInstrumentationComponent.builder()
                    .topGamingAllTimePostsFeatureInstrumentationModule(
                            TopGamingAllTimePostsFeatureInstrumentationModule(
                                    this, contentView, errorView, progressView, guideView))
                    .build()

    override fun buildPostDetailFeatureComponent(textView: TextView, imageView: ImageView)
            : PostDetailFeatureComponent = DaggerPostDetailFeatureInstrumentationComponent.builder()
            .postDetailFeatureInstrumentationModule(PostDetailFeatureInstrumentationModule(this))
            .build()
}
