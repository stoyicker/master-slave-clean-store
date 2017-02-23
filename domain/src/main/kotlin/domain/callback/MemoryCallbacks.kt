package domain.callback

/**
 * Memory-related callbacks.
 */
interface MemoryCallbacks {
    /**
     * Requests a trim of memory consumption. Implementation are free to consider different actions
     * based on the given urgency.
     * @see Urgency
     */
    fun onTrimMemory(urgency: Urgency)
}

/**
 * An enum to express priority.
 * @see MemoryCallbacks.onTrimMemory
 */
enum class Urgency {
    LOW, MEDIUM, HIGH
}
