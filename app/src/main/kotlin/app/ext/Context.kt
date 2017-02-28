package app.ext

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.IntegerRes

/**
 * Extension function to reduce verbosity to getInteger(...).
 * @param resId The id of the resource to get.
 */
internal fun Context.getInteger(@IntegerRes resId: Int) = this.resources.getInteger(resId)

/**
 * Extension function to reduce verbosity to getColor(...).
 * @param resId The id of the resource to get.
 */
internal fun Context.getColorCompat(@ColorRes resId: Int) =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            this.getColor(resId)
        else
            @Suppress("DEPRECATION")
            this.resources.getColor(resId)

/**
 * Extension function to reduce verbosity to getDimension(...).
 * @param resId The id of the resource to get.
 */
internal fun Context.getDimension(@DimenRes resId: Int) = this.resources.getDimension(resId)

/**
 * Internal function to check if the device is in portrait mode.
 */
internal fun Context.isPortrait(): Boolean {
    val displayMetrics = Resources.getSystem().displayMetrics
    return displayMetrics.heightPixels > displayMetrics.widthPixels
}
