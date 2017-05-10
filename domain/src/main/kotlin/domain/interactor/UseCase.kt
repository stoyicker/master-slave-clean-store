package domain.interactor

import domain.Domain
import domain.exec.PostExecutionThread
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.subscriptions.Subscriptions

/**
 * Abstraction used to represent domain needs.
 */
abstract class UseCase<T>(private val postExecutionThread: PostExecutionThread) {
    internal lateinit var subscription: Subscription

    /**
     * Defines the observable that represents this use case.
     */
    protected abstract fun buildUseCaseObservable(): Observable<T>

    /**
     * Executes the use case.
     * @param subscriber The subscriber to notify of the results.
     */
    fun execute(subscriber: Subscriber<T>?) {
        subscription = if (subscriber == null) {
            Subscriptions.empty()
        } else {
            buildUseCaseObservable()
                    .subscribeOn(Domain.useCaseScheduler)
                    .observeOn(postExecutionThread.provideScheduler())
                    .subscribe(subscriber)
        }
    }

    /**
     * Tears down the use case.
     */
    fun terminate() {
        if (!subscription.isUnsubscribed) {
            subscription.unsubscribe()
        }
    }
}
