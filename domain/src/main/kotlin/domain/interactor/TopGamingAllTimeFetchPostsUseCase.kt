package domain.interactor

import domain.Domain
import domain.exec.PostExecutionThread

/**
 * A use case for fetching posts (looking first at the network, if available).
 */
class TopGamingAllTimeFetchPostsUseCase(
        page: Int,
        postExecutionThread: PostExecutionThread)
    : TopGamingAllTimePostsUseCase(page, postExecutionThread) {
    override fun buildUseCase() = Domain.topPostsFacade.fetchTop(SUBREDDIT, TIME_RANGE, safePage)
}
