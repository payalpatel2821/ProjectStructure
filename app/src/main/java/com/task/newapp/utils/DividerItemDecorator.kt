package com.task.newapp.utils

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecorator(divider: Drawable) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft: Int = parent.getPaddingLeft()
        val dividerRight: Int = parent.getWidth() - parent.getPaddingRight()
        val childCount: Int = parent.getChildCount()
        for (i in 0..childCount - 2) {
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child.getLayoutParams() as RecyclerView.LayoutParams
            val dividerTop: Int = child.getBottom() + params.bottomMargin
            val dividerBottom: Int = dividerTop + mDivider.getIntrinsicHeight()
            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider.draw(canvas)
        }
    }

    init {
        mDivider = divider
    }
}