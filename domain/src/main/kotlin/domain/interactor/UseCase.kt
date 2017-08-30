package domain.interactor

/**
 * Abstraction used to represent domain needs.
 */
internal interface UseCase<out T> {
    fun buildUseCase(): T
}