package app.detail

internal interface TextAndImageView<in T : Any?> {
    /**
     * Called to notify the implementation that the content should be updated.
     */
    fun updateContent(item: T)
}
