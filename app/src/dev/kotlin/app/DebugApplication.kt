package app

import android.os.StrictMode

/**
 * Debug application, for (guess what?!) debugging purposes.
 */
internal class DebugApplication : MainApplication() {
    override fun onCreate() {
        super.onCreate()
        enforceThreadStrictMode()
        enforceVMStrictMode()
    }

    /**
     * Enforces StrictMode for the current thread.
     * @see <a href="https://developer.android.com/reference/android/os/StrictMode.html">
     *     Strict Mode | Android Developers</a>
     */
    private fun enforceThreadStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build())
    }

    /**
     * Enforces StrictMode for the VM.
     * @see <a href="https://developer.android.com/reference/android/os/StrictMode.html">
     *     Strict Mode | Android Developers</a>
     */
    private fun enforceVMStrictMode() {
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
    }
}
