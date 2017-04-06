package util.android

import android.support.annotation.VisibleForTesting
import android.support.annotation.WorkerThread
import java.io.File

/**
 * A key-value store where the keys are integers. This store can be restored from disk and the
 * stored representation is updated on writes.
 */
class IndexedPersistedByDiskStore<Value : Any?>(
        private val source: File,
        private val valueStringifier: ValueStringifier<Value>,
        private val delegate: MutableMap<Int, Value?>)
    : MutableMap<Int, Value?> by delegate {
    override fun clear() {
        delegate.clear()
        this.persist()
    }

    override fun put(key: Int, value: Value?): Value? {
        val ret = delegate.put(key, value)
        this.persist()
        return ret
    }

    override fun remove(key: Int): Value? {
        val ret = delegate.remove(key)
        this.persist()
        return ret
    }

    /**
     * Restores the store from disk by combining its current entries with the information persisted.
     * On conflict, memory information takes preference.
     */
    @WorkerThread
    fun restore() {
        if (source.exists()) {
            source.readLines(CHARSET).forEach {
                it.substringBefore("=").toInt().let { key ->
                    if (delegate[key] == null) {
                        delegate.put(key, valueStringifier.fromString(it.substringAfter("=")))
                    }
                }
            }
        }
    }

    /**
     * Persists the store to disk, creating the source file and its parent folders as necessary.
     */
    @WorkerThread
    private fun persist() {
        source.parentFile.mkdirs()
        source.writeText(
                delegate.mapNotNull {
                    "${it.key}=${valueStringifier.toString(it.value)}"
                }.joinToString(separator = "\n"),
                CHARSET)
    }

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal val CHARSET = Charsets.UTF_8
    }

    /**
     * An interface to define descriptions of types that can be saved and restored to/from Strings.
     */
    interface ValueStringifier<T> {
        /**
         * Implementations should be able to deterministically create a unique object representing
         * the given unique String representation.
         * @param source The description to rebuild from.
         * @return The built representation.
         */
        fun fromString(source: String?): T?

        /**
         * Implementations should be able to deterministically create a per-object unique String
         * representation of the instance passed as parameter.
         */
        fun toString(source: T?): String?
    }
}
