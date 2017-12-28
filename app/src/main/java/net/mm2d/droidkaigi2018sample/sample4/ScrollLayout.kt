/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample4

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
class ScrollLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private var touchSlop: Float = 0f
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var dragging: Boolean = false
    private var prevX: Float = 0f
    private var prevY: Float = 0f

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                dragging = false
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (!dragging && distance(event.rawX - startX, event.rawY - startY) > touchSlop) {
                    dragging = true
                }
            }
        }
        prevX = event.rawX
        prevY = event.rawY
        return dragging
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                dragging = false
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragging) {
                    moveOffset(event.rawX - prevX, event.rawY - prevY)
                } else if (distance(event.rawX - startX, event.rawY - startY) > touchSlop) {
                    dragging = true
                }
            }
        }
        prevX = event.rawX
        prevY = event.rawY
        return true
    }

    private fun moveOffset(dx: Float, dy: Float) {
        for (i in 0 until childCount) {
            moveOffset(getChildAt(i), dx, dy)
        }
    }

    private fun moveOffset(v: View, dx: Float, dy: Float) {
        val transitionX = v.translationX + dx
        val transitionY = v.translationY + dy
        v.translationX = clamp(transitionX, 0f, (width - v.width).toFloat())
        v.translationY = clamp(transitionY, 0f, (height - v.height).toFloat())
    }

    companion object {
        private fun clamp(value: Float, min: Float, max: Float): Float {
            return Math.min(Math.max(value, min), max)
        }

        private fun distance(x: Float, y: Float): Float {
            return Math.sqrt((x * x + y * y).toDouble()).toFloat()
        }
    }
}