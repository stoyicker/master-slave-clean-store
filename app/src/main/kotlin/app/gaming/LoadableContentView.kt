package app.gaming

/**
 * An interface describing the behavior required by views bound to coordinators.
 */
internal interface LoadableContentView<in T : Any?> {
    /**
     * Called to notify the implementation that the loading layout should be shown.
     */
    fun showLoadingLayout()

    /**
     * Called to notify the implementation that the loading layout should be hidden.
     */
    fun hideLoadingLayout()

    /**
     * Called to notify the implementation that the content should be updated.
     * @param actionResult A list of items that turned up as the outcome the executed action.
     */
    fun updateContent(actionResult: List<T>)

    /**
     * Called to notify the implementation that the content layout should be hidden. Optional.
     */
    fun hideContentLayout() { }

    /**
     * Called to notify the implementation that the error layout should be shown.
     */
    fun showErrorLayout()

    /**
     * Called to notify the implementation that the error layout should be hidden.
     */
    fun hideErrorLayout()
}
