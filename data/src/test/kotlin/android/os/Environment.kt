package android.os

import java.io.File

object Environment {
    @JvmStatic
    fun getExternalStorageDirectory() = File("/")
}

