package app.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView


/**
 * An ImageView that allows drawable upscaling to satisfy the fitCenter scaleType.
 * @see <a href="https://github.com/triposo/barone/blob/391e533f319f010b304596d4429d03edb1e7dc96/src/com/triposo/barone/ScalingImageView.java">
 *     ScalingImageView</>
 */
internal class OutScalingImageView(context: Context, attrs: AttributeSet?)
    : ImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var localHeightMeasureSpec = heightMeasureSpec
        val mDrawable = drawable
        if (mDrawable != null) {
            val mDrawableWidth = mDrawable.intrinsicWidth
            val mDrawableHeight = mDrawable.intrinsicHeight
            val actualAspect = mDrawableWidth.toFloat() / mDrawableHeight.toFloat()

            // Assuming the width is ok, so we calculate the height.
            val actualWidth = View.MeasureSpec.getSize(widthMeasureSpec)
            val height = (actualWidth / actualAspect).toInt()
            localHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                    View.MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, localHeightMeasureSpec)
    }
}
