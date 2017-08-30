package app.gaming

import app.common.UIPostExecutionThread
import domain.interactor.TopGamingAllTimePostsUseCase

/**
 * Takes care of binding the logic of the top gaming posts request to the view that handles its
 * outcome.
 * @param view The view associated to this object.
 */
internal class TopGamingAllTimePostsCoordinator(
        internal val view: TopGamingAllTimePostsView,
        private val useCaseFactory: TopGamingAllTimePostsUseCase.Factory,
        private val pageLoadSubscriberFactory: PageLoadSubscriber.Factory) {
    internal var page = 0
    private var ongoingUseCase: TopGamingAllTimePostsUseCase? = null

    /**
     * Triggers the load of the next page.
     * @param requestedManually In order to decide whether or not to resort to the cache, a boolean
     * indicating if this load was triggered manually. Defaults to <code>false</code>, which
     * resorts to memory and disk cache, checking for data availability in that order.
     */
    internal fun actionLoadNextPage(requestedManually: Boolean = true) {
        val ongoingUseCase = if (requestedManually) {
            useCaseFactory.newFetch(page, UIPostExecutionThread)
        } else {
            useCaseFactory.newGet(page, UIPostExecutionThread)
        }
        ongoingUseCase.execute(pageLoadSubscriberFactory.newSubscriber(this))
    }

    /**
     * Aborts the on-going next page load, if any.
     */
    internal fun abortActionLoadNextPage() {
        ongoingUseCase?.dispose()
    }
}
