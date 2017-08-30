package domain.interactor

import domain.Domain
import domain.entity.Post
import domain.entity.TimeRange
import domain.exec.PostExecutionThread
import kotlin.properties.Delegates

/**
 * A use case for loading all-time top posts in r/gaming. If this app was to support different
 * subreddits or time ranges I would let an upper layer choose which, but because they are
 * constrained at the business level they make more sense here.
 * @param page The page to load, 0-indexed.
 * @param postExecutionThread A representation of thread to receive the results of the execution.
 */
abstract class TopGamingAllTimePostsUseCase(
        page: Int,
        postExecutionThread: PostExecutionThread)
    : SingleDisposableUseCase<Iterable<Post>>(
        asyncExecutionScheduler = Domain.useCaseScheduler,
        postExecutionScheduler = postExecutionThread.scheduler()) {
    // This makes sure we do not try to request negative pages
    protected var safePage: Int by Delegates.vetoable(0, { _, _, new -> new >= 0 })

    init {
        safePage = page
    }

    companion object {
        const val SUBREDDIT = "gaming"
        val TIME_RANGE = TimeRange.ALL_TIME
    }

    /**
     * Description of a factory that creates instances of implementations of this use case.
     */
    interface Factory {
        /**
         * Factory method for the 'fetch' use case.
         */
        fun newFetch(page: Int,
                     postExecutionThread: PostExecutionThread): TopGamingAllTimePostsUseCase

        /**
         * Factory method for the 'get' use case.
         */
        fun newGet(page: Int,
                   postExecutionThread: PostExecutionThread): TopGamingAllTimePostsUseCase
    }
}
