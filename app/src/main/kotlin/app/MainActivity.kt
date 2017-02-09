package app

import android.app.Activity
import android.os.Bundle
import android.util.Log
import org.jorge.ms.domain.HelloWorldKotlin

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HelloWorldKotlin().let { Log.d("BANANAS", it.toString()) }
    }
}
