package app.gaming

import android.support.annotation.VisibleForTesting
import app.LoadableContentView
import app.UIPostExecutionThread
import domain.entity.Post
import domain.interactor.TopGamingAllTimePostsUseCase
import domain.interactor.UseCase
import rx.Subscriber

/**
 * Takes care of binding the logic of the top gaming posts request to the view that shows its
 * outcome.
 * @param view The view associated to this object.
 */
internal class TopGamingListCoordinator(private val view: LoadableContentView<Post>) {
    private var page = -1
    private var ongoingUseCase: UseCase<Post>? = null

    /**
     * Triggers the load of the next page.
     */
    internal fun actionLoadNextPage() {
        ongoingUseCase = TopGamingAllTimePostsUseCase(++page, UIPostExecutionThread)
        ongoingUseCase!!.execute(NextPageLoadSubscriber())
    }

    /**
     * Aborts the on-going next page load, if any.
     */
    internal fun abortActionLoadNextPage() {
        ongoingUseCase?.terminate()
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
            ongoingUseCase = null
            view.hideLoadingLayout()
            view.hideContentLayout()
            view.showErrorLayout(throwable)
        }

        override fun onCompleted() {
            ongoingUseCase = null
            view.hideLoadingLayout()
            // * is the spread operator. We use it just to build an immutable list
            view.showContentLayout(listOf(*posts.toTypedArray()))
            view.hideErrorLayout()
        }
    }
}
