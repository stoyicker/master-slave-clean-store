package app.gaming

import com.google.firebase.crash.FirebaseCrash
import domain.entity.Post
import rx.Subscriber

/**
 * The subscriber that will react to the outcome of the associated use case and request the
 * view to update itself.
 */
internal class PageLoadSubscriber(
        private val coordinator: TopGamingAllTimePostsCoordinator) : Subscriber<Post>() {
    val posts = mutableListOf<Post>()

    override fun onStart() {
        coordinator.view.showLoadingLayout()
        coordinator.view.hideContentLayout()
        coordinator.view.hideErrorLayout()
    }

    override fun onNext(post: Post?) {
        if (post != null) {
            posts.add(post)
        }
    }

    override fun onError(throwable: Throwable?) {
        FirebaseCrash.report(throwable)
        coordinator.view.showErrorLayout()
        coordinator.view.hideLoadingLayout()
        coordinator.view.hideContentLayout()
    }

    override fun onCompleted() {
        coordinator.page++
        // * is the spread operator. We use it to build an immutable list.
        coordinator.view.updateContent(listOf(*posts.toTypedArray()))
        coordinator.view.hideLoadingLayout()
        coordinator.view.hideErrorLayout()
    }

    /**
     * Description of a factory that creates page load subscribers.
     */
    internal interface Factory {
        fun newSubscriber(coordinator: TopGamingAllTimePostsCoordinator): Subscriber<Post>
    }
}
