package com.task.newapp.utils.socialtextview

import android.graphics.RectF
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView

class AccurateMovementMethod : LinkMovementMethod() {
    private val touchBounds = RectF()
    private var pressedSpan: TouchableSpan? = null
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressedSpan = getTouchedSpan(widget, buffer, event)
                if (pressedSpan != null) {
                    pressedSpan!!.setPressed(true)
                    Selection.setSelection(buffer, buffer.getSpanStart(pressedSpan), buffer.getSpanEnd(pressedSpan))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val pressedSpan2 = getTouchedSpan(widget, buffer, event)
                if (pressedSpan != null && pressedSpan2 !== pressedSpan) {
                    pressedSpan!!.setPressed(false)
                    pressedSpan = null
                    Selection.removeSelection(buffer)
                }
            }
            else -> {
                if (pressedSpan != null) {
                    pressedSpan!!.setPressed(false)
                    super.onTouchEvent(widget, buffer, event)
                }
                pressedSpan = null
                Selection.removeSelection(buffer)
            }
        }
        return true
    }

    /**
     * Gets the span that was touched.
     *
     * @param tv   [TextView]
     * @param span [Spannable]
     * @param e    [MotionEvent]
     * @return [TouchableSpan]
     */
    private fun getTouchedSpan(tv: TextView, span: Spannable, e: MotionEvent): TouchableSpan? {
        // Find the location in which the touch was made
        var x = e.x.toInt()
        var y = e.y.toInt()

        // Ignore padding
        x -= tv.totalPaddingLeft
        y -= tv.totalPaddingTop

        // Account for scrollable text
        x += tv.scrollX
        y += tv.scrollY
        val layout = tv.layout
        val touchedLine = layout.getLineForVertical(y)
        val touchOffset = layout.getOffsetForHorizontal(touchedLine, x.toFloat())

        // Set bounds of the touched line
        touchBounds.left = layout.getLineLeft(touchedLine)
        touchBounds.top = layout.getLineTop(touchedLine).toFloat()
        touchBounds.right = layout.getLineRight(touchedLine)
        touchBounds.bottom = layout.getLineBottom(touchedLine).toFloat()

        // Ensure the span falls within the bounds of the touch
        var touchSpan: TouchableSpan? = null
        if (touchBounds.contains(x.toFloat(), y.toFloat())) {
            // Find clickable spans that lie under the touched area
            val spans = span.getSpans(touchOffset, touchOffset, TouchableSpan::class.java)
            touchSpan = if (spans.isNotEmpty()) spans[0] else null
        }
        return touchSpan
    }

    companion object {
        @JvmStatic
        @get:Synchronized
        var instance: AccurateMovementMethod? = null
            get() {
                if (field == null) {
                    field = AccurateMovementMethod()
                }
                return field
            }
            private set
    }
}