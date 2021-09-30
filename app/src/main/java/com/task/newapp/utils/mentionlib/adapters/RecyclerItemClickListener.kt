package com.percolate.mentions.sample.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import android.view.GestureDetector
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View

/**
 * An onClick listener for items in a RecyclerView. This was taken from the sample provided in the StickyHeadersRecyclerView library.
 * https://github.com/timehop/sticky-headers-recyclerview/blob/master/sample/src/main/java/com/timehop/stickyheadersrecyclerview/sample/RecyclerItemClickListener.java
 */
open class RecyclerItemClickListener(context: Context?, private val mListener: OnItemClickListener?) : OnItemTouchListener {
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    private val mGestureDetector: GestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }
    })

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // do nothing
    }

}