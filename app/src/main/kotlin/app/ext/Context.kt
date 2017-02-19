package app.ext

import android.content.Context
import android.support.annotation.DimenRes
import android.support.annotation.IntegerRes

/**
 * Extension function to reduce verbosity to getInteger(...).
 * @param resId The id of the resource to get.
 */
internal fun Context.getInteger(@IntegerRes resId: Int) = this.resources.getInteger(resId)

/**
 * Extension function to reduce verbosity to getDimension(...).
 * @param resId The id of the resource to get.
 */
internal fun Context.getDimension(@DimenRes resId: Int) = this.resources.getDimension(resId)
