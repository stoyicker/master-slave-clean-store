package domain.interactor

import io.reactivex.disposables.Disposable

abstract class DisposableUseCase protected constructor() {
    protected var assembledSubscriber: Disposable? = null

    /**
     * Tears down the use case if required.
     */
    fun dispose() {
        if (assembledSubscriber?.isDisposed == false) {
            assembledSubscriber!!.dispose()
        }
    }
}
