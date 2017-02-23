package domain.exec

import rx.Scheduler

/**
 * Describes the thread where the results of an UseCase will be published.
 * @see domain.interactor.UseCase
 */
interface PostExecutionThread {
    /**
     * The underlying scheduler to feed the observable from the UseCase into.
     */
    fun provideScheduler(): Scheduler
}
