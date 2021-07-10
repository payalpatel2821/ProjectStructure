package com.task.newapp.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import com.task.newapp.R

class SegmentedRadioGroup : RadioGroup {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onFinishInflate() {
        super.onFinishInflate()
       // changeButtonsImages()
    }

    private fun changeButtonsImages() {
        val count = super.getChildCount()
        if (count > 1) {
            super.getChildAt(0)
                .setBackgroundResource(R.drawable.segment_radio_left)
            super.getChildAt(1)
                .setBackgroundResource(R.drawable.segment_radio_right)
        }
    }
}