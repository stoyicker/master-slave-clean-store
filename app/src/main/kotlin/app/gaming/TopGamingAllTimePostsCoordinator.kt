package app.gaming

import android.support.annotation.VisibleForTesting
import app.UIPostExecutionThread
import app.widget.LoadableContentView
import com.google.firebase.crash.FirebaseCrash
import domain.entity.Post
import domain.interactor.TopGamingAllTimeFetchPostsUseCase
import domain.interactor.TopGamingAllTimeGetPostsUseCase
import domain.interactor.UseCase
import rx.Subscriber

/**
 * Takes care of binding the logic of the top gaming posts request to the view that handles its
 * outcome.
 * @param view The view associated to this object.
 */
internal class TopGamingAllTimePostsCoordinator(private val view: LoadableContentView<Post>) {
    private var page = 0
    private lateinit var ongoingUseCase: UseCase<Post>

    /**
     * Triggers the load of the next page.
     * @param requestedManually In order to decide whether or not to resort to the cache, a boolean
     * indicating if this load was triggered manually. Defaults to <code>false</code>, which
     * resorts to memory and disk cache, checking for data availability in that order.
     */
    internal fun actionLoadNextPage(requestedManually: Boolean = true) {
        ongoingUseCase = if (requestedManually) {
            TopGamingAllTimeFetchPostsUseCase(page, UIPostExecutionThread)
        } else {
            TopGamingAllTimeGetPostsUseCase(page, UIPostExecutionThread)
        }
        ongoingUseCase.execute(NextPageLoadSubscriber())
    }

    /**
     * Aborts the on-going next page load, if any.
     */
    internal fun abortActionLoadNextPage() {
        ongoingUseCase.terminate()
    }

    /**
     * The subscriber that will react to the outcome of the associated use case and request the
     * view to update itself.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal inner class NextPageLoadSubscriber : Subscriber<Post>() {
        val posts = mutableListOf<Post>()

        override fun onStart() {
            view.showLoadingLayout()
            view.hideContentLayout()
            view.hideErrorLayout()
        }

        override fun onNext(post: Post?) {
            if (post != null) {
                posts.add(post)
            }
        }

        override fun onError(throwable: Throwable?) {
            FirebaseCrash.report(throwable)
            view.showErrorLayout()
            view.hideLoadingLayout()
            view.hideContentLayout()
        }

        override fun onCompleted() {
            page++
            // * is the spread operator. We use it just to build an immutable list
            view.updateContent(listOf(*posts.toTypedArray()))
            view.hideLoadingLayout()
            view.hideErrorLayout()
        }
    }
}
