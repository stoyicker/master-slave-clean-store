package app

/**
 * An interface describing the behavior required by views bound to presenters.
 */
internal interface LoadableContentView<in T : Any?> {
    fun showLoadingLayout()

    fun hideLoadingLayout()

    fun showContentLayout(actionResult: List<T>)

    fun hideContentLayout()

    fun showErrorLayout(throwable: Throwable?)

    fun hideErrorLayout()
}
