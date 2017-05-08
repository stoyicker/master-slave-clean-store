package app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import org.jorge.ms.app.R
import util.android.ext.getColorCompat
import util.android.ext.getInteger

/**
 * @see <a href="https://androidreclib.wordpress.com/2014/11/18/the-touch-ripple-on-gingerbread/">
 *     The touch ripple on Gingerbread | Android Rec Library</>
 */
internal class CompatRippleLinearLayout(context: Context, attrs: AttributeSet?)
    : LinearLayout(context, attrs) {
    private val rippleColor = context.getColorCompat(R.color.default_ripple)
    private val rippleDuration = context.getInteger(android.R.integer.config_shortAnimTime).toLong()
    private lateinit var sourceCoordinates: PointF
    private val ripplePaint = Paint()
    private var maxBoundary: Int = 0

    init {
        ripplePaint.color = rippleColor
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            sourceCoordinates = PointF(event.x, event.y)
            maxBoundary = Math.max(width, height)
            val animation = BoundedAnimation(0, maxBoundary)
            animation.duration = rippleDuration
            startAnimation(animation)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val animation = animation
        if (!isInEditMode && animation != null && animation is BoundedAnimation
                && !animation.hasEnded()) {
            val size = maxBoundary * animation.lastInterpolation
            ripplePaint.alpha = (127 * (1 - animation.lastInterpolation)).toInt()
            canvas.drawCircle(sourceCoordinates.x, sourceCoordinates.y, size, ripplePaint)
        }
        super.dispatchDraw(canvas)
    }

    /**
     * An animation for equal distribution between two given values that invalidates the layout on
     * tick.
     * @param from The starting value.
     * @param to The finishing value.
     */
    private inner class BoundedAnimation(private val from: Int, private val to: Int) : Animation() {
        internal var lastInterpolation: Float = 0.toFloat()

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            lastInterpolation = interpolatedTime
            this@CompatRippleLinearLayout.invalidate()
            super.applyTransformation(interpolatedTime, t)
        }
    }
}
