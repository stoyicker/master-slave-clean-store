package app.gaming

import android.content.Context
import android.support.annotation.Px
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import org.jorge.ms.app.R
import util.android.ext.getDimension

/**
 * A recycler view that automatically attaches to itself a GridLayoutManager and performs auto-fit
 * with its elements.
 * @param context The context
 * @param attrs The attributes
 * @see <a href="http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html">
 *     Square Island: RecyclerView: Autofit grid</a>
 */
internal class AutoFitStaggeredGridRecyclerView(context: Context, attrs: AttributeSet?)
    : RecyclerView(context, attrs) {
    private @Px val columnWidth: Int

    init {
        @Px val defaultColumnWidth = context.getDimension(R.dimen.default_column_width).toInt()
        if (attrs != null) {
            val attrsArray = intArrayOf(android.R.attr.columnWidth)
            val typedArray = context.obtainStyledAttributes(attrs, attrsArray)
            columnWidth = typedArray.getDimensionPixelSize(0, defaultColumnWidth)
            typedArray.recycle()
        } else {
            columnWidth = defaultColumnWidth
        }
        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (!isInEditMode && columnWidth > 0) {
            val spanCount = Math.max(1, measuredWidth / columnWidth)
            (layoutManager as StaggeredGridLayoutManager).spanCount = spanCount
        }
    }
}
