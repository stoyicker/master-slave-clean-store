package util.android.test

import android.support.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

/**
 * https://github.com/googlesamples/android-testing/blob/adb986a1f67ce0c69927c72cbf4107372d49121e/ui/espresso/IdlingResourceSample/app/src/main/java/com/example/android/testing/espresso/IdlingResourceSample/IdlingResource/SimpleIdlingResource.java
 */
class BinaryIdlingResource(private val name: String) : IdlingResource {
    private val isIdle = AtomicBoolean(true)
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName() = name

    override fun isIdleNow() = isIdle.get()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }

    /**
     * Sets the idle state of the resource and notifies the bound callback, if any.
     * @param isIdleNow The new idle state for the resource.
     */
    fun setIdleState(isIdleNow: Boolean) {
        isIdle.set(isIdleNow)
        if (isIdleNow) {
            resourceCallback?.onTransitionToIdle()
        }
    }
}
