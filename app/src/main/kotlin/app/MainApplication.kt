package app

import android.annotation.SuppressLint
import android.app.Application
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.detail.DaggerPostDetailFeatureComponent
import app.detail.PostDetailFeatureComponent
import app.detail.PostDetailFeatureModule
import app.gaming.DaggerTopGamingAllTimePostsFeatureComponent
import app.gaming.TopGamingAllTimePostsFeatureComponent
import app.gaming.TopGamingAllTimePostsFeatureModule
import data.top.TopPostsFacade
import domain.Domain

/**
 * Custom application.
 */
@SuppressLint("Registered") // Registered via buildType-specific manifests
internal open class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Domain.topPostsFacade(TopPostsFacade())
    }

    /**
     * Objects related to this feature can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.gaming.TopGamingAllTimePostsActivity
     */
    open fun buildTopGamingAllTimePostsFeatureComponent(
            contentView: RecyclerView, errorView: View, progressView: View, guideView: View):
            TopGamingAllTimePostsFeatureComponent = DaggerTopGamingAllTimePostsFeatureComponent
            .builder()
            .topGamingAllTimePostsFeatureModule(TopGamingAllTimePostsFeatureModule(
                                    this, contentView, errorView, progressView, guideView))
            .build()


    /**
     * Objects related to this feature can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.detail.PostDetailActivity
     */
    open fun buildPostDetailFeatureComponent(textView: TextView, imageView: ImageView)
            : PostDetailFeatureComponent = DaggerPostDetailFeatureComponent.builder()
                    .postDetailFeatureModule(PostDetailFeatureModule(this, textView, imageView))
                    .build()
}
