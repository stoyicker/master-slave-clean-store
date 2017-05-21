package app.detail

import android.content.Context
import app.share.ShareFeature
import dagger.Component
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import javax.inject.Singleton

/**
 * Module used to provide stuff required by PostDetailActivity.
 * @see PostDetailActivity
 */
@Module
internal class PostDetailFeatureInstrumentationModule(private val context: Context) {
    @Provides
    fun postDetailView(): PostDetailView = mock(PostDetailView::class.java)

    @Provides
    fun shareFeature() = ShareFeature(context)
}

/**
 * A component to inject instances that require access to dependencies provided by
 * PostDetailFeatureModule.
 * @see PostDetailFeatureModule
 */
@Component(modules = arrayOf(PostDetailFeatureInstrumentationModule::class))
@Singleton
internal interface PostDetailFeatureInstrumentationComponent : PostDetailFeatureComponent

