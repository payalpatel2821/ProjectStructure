package com.task.newapp.utils.shadowpost

import android.util.TypedValue
import android.view.View

internal fun View.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}