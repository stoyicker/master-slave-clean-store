package app.gaming

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.entity.Post
import domain.exec.PostExecutionThread
import domain.interactor.TopGamingAllTimeFetchPostsUseCase
import domain.interactor.TopGamingAllTimeGetPostsUseCase
import domain.interactor.TopGamingAllTimePostsUseCase
import org.jorge.ms.app.BuildConfig
import javax.inject.Singleton

/**
 * A component to inject instances that require access to dependencies provided by
 * TopGamingAllTimePostsFeatureModule.
 * @see TopGamingAllTimePostsFeatureModule
 */
@Component(modules = arrayOf(TopGamingAllTimePostsFeatureModule::class))
@Singleton
internal interface TopGamingAllTimePostsFeatureComponent {
    fun inject(target: TopGamingAllTimePostsActivity)
    fun inject(target: TopGamingAllTimePostsFeatureView)
}

/**
 * Module used to provide stuff required by TopGamingAllTimePostsActivity.
 * @see TopGamingAllTimePostsActivity
 */
@Module
internal class TopGamingAllTimePostsFeatureModule(
        private val contentView: RecyclerView,
        private val errorView: View,
        private val progressView: View,
        private val activity: Activity) {
    @Provides
    @Singleton
    fun coordinatorBehaviorCallback(coordinator: TopGamingAllTimePostsCoordinator) =
            object : TopGamingAllTimePostsActivity.BehaviorCallback {
                @SuppressLint("InlinedApi")
                override fun onItemClicked(item: Post) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.detailLink))
                    // https://developer.android.com/training/implementing-navigation/descendant.html#external-activities
                    if (BuildConfig.VERSION_CODE > Build.VERSION_CODES.LOLLIPOP) {
                        @Suppress("DEPRECATION")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    } else {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                    }
                    val candidates = activity.packageManager
                            .queryIntentActivities(intent, 0)
                    if (candidates.size > 0) {
                        activity.startActivity(intent)
                    }
                }

                override fun onPageLoadRequested() {
                    coordinator.actionLoadNextPage()
                }
            }

    @Provides
    @Singleton
    fun pageLoadSubscriberFactory() = object : PageLoadSubscriber.Factory {
        override fun newSubscriber(coordinator: TopGamingAllTimePostsCoordinator) =
                PageLoadSubscriber(coordinator)
    }

    @Provides
    @Singleton
    fun topGamingAllTimePostsCoordinator(view: TopGamingAllTimePostsView,
                                         useCaseFactory: TopGamingAllTimePostsUseCase.Factory,
                                         pageLoadSubscriberFactory: PageLoadSubscriber.Factory) =
            TopGamingAllTimePostsCoordinator(view, useCaseFactory, pageLoadSubscriberFactory)

    @Provides
    @Singleton
    fun topGamingAllTimePostsUseCaseFactory() = object : TopGamingAllTimePostsUseCase.Factory {
        override fun newFetch(page: Int, postExecutionThread: PostExecutionThread) =
                TopGamingAllTimeFetchPostsUseCase(page, postExecutionThread)

        override fun newGet(page: Int, postExecutionThread: PostExecutionThread) =
                TopGamingAllTimeGetPostsUseCase(page, postExecutionThread)
    }

    @Provides
    @Singleton
    fun topGamingAllTimePostsView() = TopGamingAllTimePostsView(
            contentView, errorView, progressView)

    @Provides
    @Singleton
    fun viewConfig(
            view: TopGamingAllTimePostsView,
            callback: TopGamingAllTimePostsActivity.BehaviorCallback) =
            TopGamingAllTimePostsFeatureView(view, callback)
}
