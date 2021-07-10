package com.task.newapp.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class SquareRelativeLayout : RelativeLayout {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize: Int = MeasureSpec.getSize(heightMeasureSpec)
        val size: Int
        size = if (widthMode == MeasureSpec.EXACTLY && widthSize > 0) {
            widthSize
        } else if (heightMode == MeasureSpec.EXACTLY && heightSize > 0) {
            heightSize
        } else {
            if (widthSize < heightSize) widthSize else heightSize
        }
        val finalMeasureSpec: Int = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(finalMeasureSpec, finalMeasureSpec)
    }
}