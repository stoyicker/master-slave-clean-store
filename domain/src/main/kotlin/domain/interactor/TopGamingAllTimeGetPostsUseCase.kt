package domain.interactor

import domain.Domain
import domain.exec.PostExecutionThread

/**
 * A use case for getting posts (looking first at the cache).
 */
class TopGamingAllTimeGetPostsUseCase(
        page: Int,
        postExecutionThread: PostExecutionThread)
    : TopGamingAllTimePostsUseCase(page, postExecutionThread) {
    override fun buildUseCase() = Domain.topPostsFacade.getTop(SUBREDDIT, TIME_RANGE, safePage)
}
