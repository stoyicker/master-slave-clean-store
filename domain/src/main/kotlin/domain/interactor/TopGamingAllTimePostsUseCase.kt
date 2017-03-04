package domain.interactor

import domain.Domain
import domain.entity.Post
import domain.entity.TimeRange
import domain.exec.PostExecutionThread
import rx.Observable
import kotlin.properties.Delegates

/**
 * A use case for loading all-time top posts in r/gaming. If this app was to support different
 * subreddits or time ranges I would let an upper layer choose which, but because they are
 * constrained at the business level they make more sense here.
 * @param page The page to load, 0-indexed.
 * @param postExecutionThread A representation of thread to receive the results of the execution.
 */
class TopGamingAllTimePostsUseCase(
        page: Int,
        postExecutionThread: PostExecutionThread) : UseCase<Post>(postExecutionThread) {
    // This makes sure we do not requests negative pages
    private var safePage: Int by Delegates.vetoable(0, { _, _, new -> new >= 0})

    init {
        safePage = page
    }

    override fun buildUseCaseObservable(): Observable<Post>
            = Domain.topPostsFacade.getTop(SUBREDDIT, TIME_RANGE, safePage)

    companion object {
        const val SUBREDDIT = "gaming"
        val TIME_RANGE = TimeRange.ALL_TIME
    }
}
