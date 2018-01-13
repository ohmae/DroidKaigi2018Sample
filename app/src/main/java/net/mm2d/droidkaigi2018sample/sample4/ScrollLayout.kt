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
import android.view.ViewConfiguration
import android.widget.FrameLayout
import net.mm2d.droidkaigi2018sample.util.hypotenuseSquare

/**
 * フリック操作を元に子Viewの位置を移動させる。
 *
 * 子Viewはクリックを受け付ける可能性もあれば、何もしない可能性もあり、
 * また子ViewではなくこのViewが直接タッチされる可能性がある状況で適切にイベントを伝搬させるようにする。
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ScrollLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private val touchSlopSquare: Float by lazy {
        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        (touchSlop * touchSlop).toFloat()
    }
    private var startX = 0f
    private var startY = 0f
    private var dragging = false
    private var prevX = 0f
    private var prevY = 0f

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // ACTION_DOWNは必ずコールされるためここで受け取る
                dragging = false
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE ->
                if (hypotenuseSquare(event.rawX - startX, event.rawY - startY) > touchSlopSquare) {
                    dragging = true
                }
        }
        prevX = event.rawX
        prevY = event.rawY
        // dragging状態になった時点でtrueを返し、onTouchEventでの処理を行う
        return dragging
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
        // ACTION_DOWNはonInterceptTouchEventで処理済み
            MotionEvent.ACTION_MOVE -> {
                if (dragging) {
                    moveOffset(event.rawX - prevX, event.rawY - prevY)
                } else if (hypotenuseSquare(event.rawX - startX, event.rawY - startY) > touchSlopSquare) {
                    // 子ViewがonTouchEvent()でfalseを返した場合や、直接タッチされた場合は、
                    // dragging状態になる前にonTouchEventでの処理となるため判定は両方で必要
                    dragging = true
                }
            }
        }
        prevX = event.rawX
        prevY = event.rawY
        return true
    }

    /**
     * すべての子Viewを移動させる。
     *
     * @param dx X軸方向の移動
     * @param dy Y軸方向の移動
     */
    private fun moveOffset(dx: Float, dy: Float) {
        for (i in 0 until childCount) {
            moveOffset(getChildAt(i), dx, dy)
        }
    }

    /**
     * Viewを移動させる。
     *
     * @param view 移動させるView
     * @param dx X軸方向の移動
     * @param dy Y軸方向の移動
     */
    private fun moveOffset(view: View, dx: Float, dy: Float) {
        val transitionX = view.translationX + dx
        val transitionY = view.translationY + dy
        view.translationX = clamp(transitionX, 0f, (width - view.right).toFloat())
        view.translationY = clamp(transitionY, 0f, (height - view.bottom).toFloat())
    }
}