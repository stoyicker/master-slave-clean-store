package app.gaming

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import app.UIPostExecutionThread
import app.gaming.TopGamingActivityInstrumentation.Companion.PUBLISH_SUBJECT
import app.gaming.TopGamingActivityInstrumentation.Companion.SUBSCRIBER_GENERATOR
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.entity.Post
import domain.exec.PostExecutionThread
import domain.interactor.TopGamingAllTimePostsUseCase
import org.jorge.ms.app.BuildConfig
import rx.subjects.PublishSubject
import javax.inject.Singleton

/**
 * Module used to provide stuff required by this test.
 */
@Module
internal class TopGamingAllTimePostsFeatureInstrumentationModule(
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
                    override fun buildUseCaseObservable() = PUBLISH_SUBJECT
                }

            override fun newGet(page: Int, postExecutionThread: PostExecutionThread) =
                newFetch(page, postExecutionThread)
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
            TopGamingAllTimePostsContentViewConfig(view, callback)
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
