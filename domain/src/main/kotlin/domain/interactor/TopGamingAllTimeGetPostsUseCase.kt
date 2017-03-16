package domain.interactor

import domain.Domain
import domain.entity.Post
import domain.exec.PostExecutionThread
import rx.Observable

/**
 * A use case for getting posts (looking first at the cache).
 */
class TopGamingAllTimeGetPostsUseCase(page: Int, postExecutionThread: PostExecutionThread)
    : TopGamingAllTimePostsUseCase(page, postExecutionThread) {
    override fun buildUseCaseObservable(): Observable<Post> =
            Domain.topPostsFacade.getTop(SUBREDDIT, TIME_RANGE, safePage)
}
