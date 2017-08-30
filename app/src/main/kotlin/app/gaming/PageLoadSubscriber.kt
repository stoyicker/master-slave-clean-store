package app.gaming

import app.common.PresentationEntityMapper
import app.common.PresentationPost
import domain.entity.Post
import io.reactivex.observers.DisposableSingleObserver

/**
 * The subscriber that will react to the outcome of the associated use case and request the
 * view to update itself.
 */
internal open class PageLoadSubscriber(
        private val coordinator: TopGamingAllTimePostsCoordinator)
    : DisposableSingleObserver<Iterable<Post>>() {
    private val entityMapper = PresentationEntityMapper()

    override fun onStart() {
        coordinator.view.apply {
            showLoadingLayout()
            hideContentLayout()
            hideErrorLayout()
        }
    }

    override fun onSuccess(payload: Iterable<Post>) {
        coordinator.apply {
            if (!payload.none()) {
                page++
                // * is the spread operator. We use it to build an immutable list.
                view.updateContent(listOf(*payload.map {
                    entityMapper.transform(it)
                }.toTypedArray()))
            }
            view.apply {
                hideLoadingLayout()
                hideErrorLayout()
            }
        }
    }

    override fun onError(throwable: Throwable) {
        coordinator.view.apply {
            showErrorLayout()
            hideLoadingLayout()
            hideContentLayout()
        }
    }

    /**
     * Description of a factory that creates page load subscribers.
     */
    internal interface Factory {
        fun newSubscriber(coordinator: TopGamingAllTimePostsCoordinator)
                : DisposableSingleObserver<Iterable<Post>>
    }
}
