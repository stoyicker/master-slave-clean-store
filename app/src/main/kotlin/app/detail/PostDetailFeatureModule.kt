package app.detail

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import app.share.ShareFeature
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * A component to inject instances that require access to dependencies provided by
 * PostDetailFeatureModule.
 * @see PostDetailFeatureModule
 */
@Component(modules = arrayOf(PostDetailFeatureModule::class))
@Singleton
internal interface PostDetailFeatureComponent {
    fun inject(target: PostDetailActivity)
}

/**
 * Module used to provide stuff required by PostDetailActivity.
 * @see PostDetailActivity
 */
@Module
internal class PostDetailFeatureModule(
        private val context: Context,
        private val textView: TextView,
        private val imageView: ImageView) {
    @Provides
    fun postDetailView() = PostDetailView(textView, imageView)

    @Provides
    fun shareFeature() = ShareFeature(context)
}
