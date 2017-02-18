package app.gaming

import android.support.annotation.VisibleForTesting
import app.LoadableContentView
import app.UIPostExecutionThread
import domain.entity.Post
import domain.interactor.TopGamingAllTimePostsUseCase
import rx.Subscriber

/**
 * A presenter that takes care of binding the logic of the top gaming posts request to the view
 * that shows its outcome.
 * @param view The view associated to this presenter.
 */
internal class TopGamingListPresenter(private val view: LoadableContentView<Post>) {
    private var page = -1

    /**
     * Triggers the load of the next page.
     */
    internal fun actionLoadNextPage() {
        TopGamingAllTimePostsUseCase(++page, UIPostExecutionThread)
                .execute(NextPageLoadSubscriber())
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
            view.hideLoadingLayout()
            view.hideContentLayout()
            view.showErrorLayout(throwable)
        }

        override fun onCompleted() {
            view.hideLoadingLayout()
            // * is the spread operator. We use it just to build an immutable list
            view.showContentLayout(listOf(*posts.toTypedArray()))
            view.hideErrorLayout()
        }
    }
}
