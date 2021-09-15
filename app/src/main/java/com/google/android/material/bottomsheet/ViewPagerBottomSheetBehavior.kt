package com.google.android.material.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.viewpager.widget.ViewPager
import java.lang.ref.WeakReference

class ViewPagerBottomSheetBehavior<V : View>
    : BottomSheetBehavior<V>,
    ViewPager.OnPageChangeListener {

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        val container = this.viewRef?.get() ?: return
        nestedScrollingChildRef = WeakReference(findScrollingChild(container))
    }

    @VisibleForTesting
    override fun findScrollingChild(view: View?): View? {
        return if (view is ViewPager) {
            view.focusedChild?.let { findScrollingChild(it) }
        } else {
            super.findScrollingChild(view)
        }
    }
}