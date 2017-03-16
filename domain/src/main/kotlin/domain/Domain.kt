package domain

import domain.repository.DomainTopPostsFacade
import rx.schedulers.Schedulers

/**
 * Global configuration holder for the module.
 * Note how this class acts as a dependency holder. You could also a DI framework like Dagger for
 * example, but to only provide a single dependency, which is also a singleton, might as well do it
 * myself instead.
 */
object Domain {
    internal lateinit var topPostsFacade: DomainTopPostsFacade
    internal val useCaseScheduler by lazy { Schedulers.io() }

    /**
     * Set an implemented DomainTopPostsFacade.
     * @param facade The facade to set.
     */
    fun topPostsFacade(facade: DomainTopPostsFacade) {
        topPostsFacade = facade
    }
}
