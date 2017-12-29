/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample4

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.math.MathUtils.clamp
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import net.mm2d.droidkaigi2018sample.util.calculateDistance

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ScrollLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private var touchSlop = 0f
    private var startX = 0f
    private var startY = 0f
    private var dragging = false
    private var prevX = 0f
    private var prevY = 0f

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                dragging = false
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (!dragging && calculateDistance(event.rawX - startX, event.rawY - startY) > touchSlop) {
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
                } else if (calculateDistance(event.rawX - startX, event.rawY - startY) > touchSlop) {
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
}