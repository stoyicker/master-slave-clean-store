package app

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

/**
 * A test runner to use a different Application class in the instrumentation tests. Using the
 * manifest would be overwritten by the debug manifest declaration.
 */
class AndroidTestApplicationAndroidJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, AndroidTestApplication::class.java.name, context)
    }
}
