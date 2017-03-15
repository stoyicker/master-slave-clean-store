@file:JvmName("ResettableLazyKt")

package goody

import android.support.annotation.RestrictTo
import kotlin.reflect.KProperty

/**
 * Adapted from <a href="http://stackoverflow.com/questions/35752575/kotlin-lazy-properties-and-values-reset-a-resettable-lazy-delegate">Stack Overflow</a>.
 * This implementation makes the manager a singleton instead since for me having different instances
 * of it is not a requirement as I do not want to establish any relationship between the
 * variables I will use it for (and arguably this is even incorrect in the Stack Overflow
 * implementation since the original <code>lazy</code> in the Kotlin runtime does not have this
 * functionality either).
 */
internal object ResettableLazyManager {
    private val managedDelegates = mutableMapOf<Any?, ResettableLazy<out Any>>()

    /**
     * Registers a resettable lazy property to be managed by this instance. DO NOT USE, THIS IS
     * DONE AUTOMATICALLY FOR YOU.
     * @param target The target to register.
     * @param resettable The resettable representation of the property for later resets.
     */
    fun register(target: Any, resettable: ResettableLazy<out Any>) {
        synchronized (managedDelegates) {
            managedDelegates.put(target, resettable)
        }
    }

    /**
     * Resets a property that was initialized using <code>resettableLazy<code>. Therefore, this
     * method has a side effect.
     * @param managed The property to reset. If not initialized using <code>resettableLazy<code>,
     * this method is useless.
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    fun reset(managed: Any) {
        synchronized (managedDelegates) {
            val target = managedDelegates[managed]
            target?.let {
                managedDelegates.remove(it)
                it.reset()
            }
        }
    }
}

/**
 * An interface to describe how resetting would work.
 */
internal interface Resettable {
    /**
     * Resets the value of this property. Its lazy initializer will be invoked again on next read.
     */
    fun reset()
}


/**
 * The real implementation. It just wraps a delegate which holds the actual valuable of the property
 * inside a non-resettable <code>lazy</code> block. When resetting, what is actually reset is
 * the delegate
 */
internal class ResettableLazy<T : Any>(val init: () -> T) : Resettable {
    @Volatile var lazyHolder = makeInitBlock()

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return lazyHolder.value
    }

    /**
     * Resets the value of this property. It will be re-calculated on next access.
     */
    override fun reset() {
        lazyHolder = makeInitBlock()
    }

    /**
     * This resets the delegate and re-runs the initialization function.
     */
    private fun makeInitBlock() = lazy {
        val ret = init()
        ResettableLazyManager.register(ret, this)
        ret
    }
}

/**
 * This is the function that we use to offer a similar syntax as the original <code>lazy</code>.
 */
internal fun <T : Any> resettableLazy(init: () -> T): ResettableLazy<T> {
    return ResettableLazy(init)
}
