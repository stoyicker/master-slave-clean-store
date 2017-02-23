package domain

import domain.repository.DomainTopPostsFacade
import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Global configuration holder for the module.
 * Note how this class acts as a dependency holder. You could also a DI framework like Dagger for
 * example, but to only inject a single dependency, which is also a singleton, might as well do it
 * myself instead.
 */
object Domain {
    internal lateinit var topPostsFacade: DomainTopPostsFacade
    internal var useCaseScheduler: Scheduler = Schedulers.io()

    /**
     * Inject an implemented DomainTopPostsFacade.
     * @param facade The facade to inject.
     */
    fun inject(facade: DomainTopPostsFacade) {
        topPostsFacade = facade
    }

    /**
     * Inject a Scheduler for UseCase execution (could also be configured per UseCase in more
     * complex scenarios).
     * @param scheduler The scheduler to inject.
     */
    fun inject(scheduler: Scheduler) {
        useCaseScheduler = scheduler
    }
}
