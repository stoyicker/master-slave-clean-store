package app

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.test.runner.AndroidJUnitRunner
import cucumber.api.android.CucumberInstrumentationCore

/**
 * A test runner to use a different Application class in the instrumentation tests. Using the
 * manifest would be overwritten by the debug manifest declaration.
 */
class AndroidTestApplicationAndroidJUnitRunner : AndroidJUnitRunner() {
    private val cucumberDelegate = CucumberInstrumentationCore(this)

    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, AndroidTestApplication::class.java.name, context)
    }

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        cucumberDelegate.create(arguments)
        start()
    }

    override fun onStart() {
        super.onStart()
        waitForIdleSync()
        cucumberDelegate.start()
    }
}
