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
    internal val useCaseScheduler by lazy { Provide.useCaseSchedulerGenerator() }

    /**
     * Set an implemented DomainTopPostsFacade.
     * @param facade The facade to set.
     */
    fun topPostsFacade(facade: DomainTopPostsFacade) {
        topPostsFacade = facade
    }

    internal object Provide {
        /**
         * Set a Scheduler for UseCase execution (could also be configured per UseCase in more
         * complex scenarios). The way to do it is by providing a generator function that will be
         * invoked the first time the field is accessed.
         */
        var useCaseSchedulerGenerator = DEFAULTS.USE_CASE_SCHEDULER_GENERATOR

        private object DEFAULTS {
            internal val USE_CASE_SCHEDULER_GENERATOR = { Schedulers.io() }
        }
    }
}
