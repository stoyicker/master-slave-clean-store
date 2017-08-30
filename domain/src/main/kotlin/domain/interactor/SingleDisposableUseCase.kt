package domain.interactor

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver

abstract class SingleDisposableUseCase<T> protected constructor(
        /**
         * Send null for in-place synchronous execution
         */
        private val asyncExecutionScheduler: Scheduler? = null,
        private val postExecutionScheduler: Scheduler)
    : DisposableUseCase(), UseCase<Single<T>> {
    fun execute(subscriber: DisposableSingleObserver<T>) {
        assembledSubscriber = buildUseCase().let {
            val completeSetup = { x: Single<T> ->
                x.observeOn(postExecutionScheduler).subscribeWith(subscriber)
            }
            if (asyncExecutionScheduler != null) {
                completeSetup(it.subscribeOn(asyncExecutionScheduler))
            } else {
                completeSetup(it)
            }
        }
    }
}
