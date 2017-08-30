package app.gaming

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import app.common.PresentationPost
import app.common.UIPostExecutionThread
import app.detail.PostDetailActivity
import app.gaming.TopGamingActivityInstrumentation.Companion.SUBJECT
import app.gaming.TopGamingActivityInstrumentation.Companion.SUBSCRIBER_GENERATOR
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.entity.Post
import domain.exec.PostExecutionThread
import domain.interactor.TopGamingAllTimePostsUseCase
import io.reactivex.Single
import javax.inject.Singleton

/**
 * Module used to provide stuff required by this test.
 */
@Module
internal class TopGamingAllTimePostsFeatureInstrumentationModule(
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
                SUBSCRIBER_GENERATOR(coordinator)
    }

    @Provides
    @Singleton
    fun topGamingAllTimePostsCoordinator(view: TopGamingAllTimePostsView,
                                         useCaseFactory: TopGamingAllTimePostsUseCase.Factory,
                                         pageLoadSubscriberFactory: PageLoadSubscriber.Factory) =
            TopGamingAllTimePostsCoordinator(view, useCaseFactory, pageLoadSubscriberFactory)

    @Provides
    @Singleton
    fun topGamingAllTimePostsUseCaseFactory(): TopGamingAllTimePostsUseCase.Factory =
        object : TopGamingAllTimePostsUseCase.Factory {
            override fun newFetch(page: Int, postExecutionThread: PostExecutionThread) =
                object : TopGamingAllTimePostsUseCase(page, UIPostExecutionThread) {
                    override fun buildUseCase(): Single<Iterable<Post>> = SUBJECT.singleOrError()
                }

            override fun newGet(page: Int, postExecutionThread: PostExecutionThread) =
                newFetch(page, postExecutionThread)
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

/**
 * The reason why we use a replacement component instead of inheritance in the module structure
 * is that such a solution could have some potentially bad consequences.
 * @see <a href="https://google.github.io/dagger/testing.html">Testing with Dagger</a>
 */
@Component(modules = arrayOf(TopGamingAllTimePostsFeatureInstrumentationModule::class))
@Singleton
internal interface TopGamingAllTimePostsFeatureInstrumentationComponent
    : TopGamingAllTimePostsFeatureComponent
