package app.gaming

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import app.common.PresentationPost
import app.detail.PostDetailActivity
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.exec.PostExecutionThread
import domain.interactor.TopGamingAllTimeFetchPostsUseCase
import domain.interactor.TopGamingAllTimeGetPostsUseCase
import domain.interactor.TopGamingAllTimePostsUseCase
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
}

/**
 * Module used to provide stuff required by TopGamingAllTimePostsActivity and related objects.
 * @see TopGamingAllTimePostsActivity
 */
@Module
internal class TopGamingAllTimePostsFeatureModule(
        private val context: Context,
        private val contentView: RecyclerView,
        private val errorView: View,
        private val progressView: View,
        private val guideView: View) {
    @Provides
    @Singleton
    fun coordinatorBehaviorCallback(coordinator: TopGamingAllTimePostsCoordinator) =
            object : TopGamingAllTimePostsActivity.BehaviorCallback {
                @SuppressLint("InlinedApi")
                override fun onItemClicked(item: PresentationPost) {
                    context.startActivity(PostDetailActivity.getCallingIntent(context, item))
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
            contentView, errorView, progressView, guideView)

    @Provides
    @Singleton
    fun viewConfig(
            view: TopGamingAllTimePostsView,
            callback: TopGamingAllTimePostsActivity.BehaviorCallback) =
            TopGamingAllTimePostsFeatureView(view, callback)
}
