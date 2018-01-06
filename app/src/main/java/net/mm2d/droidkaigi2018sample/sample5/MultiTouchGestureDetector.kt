/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.content.Context
import android.support.annotation.Dimension
import android.view.MotionEvent

/**
 * マルチタッチによるピンチ操作を判定するDetector
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class MultiTouchGestureDetector(context: Context, private val mListener: GestureListener) {
    @Dimension
    private val minimumSpan: Float
    @Dimension
    private var prevFocusX = 0f
    @Dimension
    private var prevFocusY = 0f
    @Dimension
    private var prevSpanX = 0f
    @Dimension
    private var prevSpanY = 0f

    interface GestureListener {
        fun onMove(@Dimension deltaX: Float, @Dimension deltaY: Float)

        fun onScale(@Dimension focusX: Float, @Dimension focusY: Float, scaleX: Float, scaleY: Float)
    }

    init {
        val density = context.resources.displayMetrics.density
        minimumSpan = Math.round(MINIMUM_SPAN * density).toFloat()
    }

    fun onTouchEvent(event: MotionEvent) {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN ->
                handleMotionEvent(event, false)
            MotionEvent.ACTION_UP ->
                handleMotionEvent(event, true)
            MotionEvent.ACTION_MOVE ->
                handleMotionEvent(event, true)
            MotionEvent.ACTION_POINTER_DOWN -> {
                // ポインタが増えたイベント
                // 今回増えたポインタ以外で一度差分計算、通知を行う
                handleMotionEvent(event, true, true)
                // 次回のイベント発生時に差分を計算するため、イベントポインタを含めて現在値の計算を行う
                handleMotionEvent(event, false, false)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                // ポインタが減るイベント
                // 次回以降なくなるポインタを含めて差分を計算し、通知を行う
                handleMotionEvent(event, true, false)
                // 次回以降なくなるポインタを除いて、現在値の計算を行う
                handleMotionEvent(event, false, true)
            }
        }
    }

    private fun handleMotionEvent(event: MotionEvent, notify: Boolean, excludeActionPointer: Boolean = false) {
        val count = event.pointerCount
        val skipIndex = if (excludeActionPointer) event.actionIndex else -1
        val div = if (excludeActionPointer) count - 1 else count

        // ポインタの中心座標を求める
        var sumX = 0f
        var sumY = 0f
        for (i in 0 until count) {
            if (i == skipIndex) continue
            sumX += event.getX(i)
            sumY += event.getY(i)
        }
        val focusX = sumX / div
        val focusY = sumY / div

        // 中心座標から各ポインタへの距離の平均をピンチ操作の基準値とする
        var devSumX = 0f
        var devSumY = 0f
        for (i in 0 until count) {
            if (i == skipIndex) continue
            devSumX += Math.abs(focusX - event.getX(i)) / div
            devSumY += Math.abs(focusY - event.getY(i)) / div
        }
        val spanX = devSumX / div * 2
        val spanY = devSumY / div * 2

        if (notify) {
            val scaleX = if (prevSpanX < minimumSpan) 1.0f else spanX / prevSpanX
            val scaleY = if (prevSpanY < minimumSpan) 1.0f else spanY / prevSpanY
            val dX = focusX - prevFocusX
            val dY = focusY - prevFocusY
            mListener.onMove(dX, dY)
            if (scaleX != 1.0f || scaleY != 1.0f) {
                mListener.onScale(focusX, focusY, scaleX, scaleY)
            }
        }
        prevSpanX = spanX
        prevSpanY = spanY
        prevFocusX = focusX
        prevFocusY = focusY
    }

    companion object {
        @Dimension(unit = Dimension.DP)
        private val MINIMUM_SPAN = 20.0f
    }
}
