package domain.interactor

import domain.Domain
import domain.entity.Post
import domain.exec.PostExecutionThread
import rx.Observable

/**
 * A use case for fetching posts (looking first at the network, if available).
 */
class TopGamingAllTimeFetchPostsUseCase(page: Int, postExecutionThread: PostExecutionThread)
    : TopGamingAllTimePostsUseCase(page, postExecutionThread) {
    override fun buildUseCaseObservable(): Observable<Post> =
            Domain.topPostsFacade.fetchTop(SUBREDDIT, TIME_RANGE, safePage)
}
