package util.android.ext

import android.content.Context
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.IntegerRes

/**
 * Extension function to reduce verbosity to resources.getInteger(...).
 * @param resId The id of the resource to get.
 */
fun Context.getInteger(@IntegerRes resId: Int) = this.resources.getInteger(resId)

/**
 * Extension function to reduce verbosity to resources.getColor(...).
 * @param resId The id of the resource to get.
 */
fun Context.getColorCompat(@ColorRes resId: Int) =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            this.getColor(resId)
        else
            @Suppress("DEPRECATION")
            this.resources.getColor(resId)

/**
 * Extension function to reduce verbosity to resources.getDimension(...).
 * @param resId The id of the resource to get.
 */
fun Context.getDimension(@DimenRes resId: Int) = this.resources.getDimension(resId)
