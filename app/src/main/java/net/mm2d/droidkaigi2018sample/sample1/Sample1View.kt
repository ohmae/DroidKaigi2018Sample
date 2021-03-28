/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample1

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Dimension
import net.mm2d.droidkaigi2018sample.R

/**
 * タッチイベントを受け取り、その座標を描画するView。
 *
 * 履歴の情報を使わないと荒い単位でしか座標がとれないことが分かります。
 */
class Sample1View @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    @Dimension
    private val radius: Float
    private val paint: Paint
    private var buffer: Bitmap? = null
    private var bufferCanvas: Canvas? = null

    private var useHistory = false

    init {
        val resources = context.resources
        radius = resources.getDimension(R.dimen.sample1_radius)
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Style.STROKE
        paint.strokeWidth = resources.getDimension(R.dimen.sample1_stroke_width)
    }

    fun clear() {
        bufferCanvas?.drawColor(Color.WHITE) ?: return
        invalidate()
    }

    fun setUseHistory(useHistory: Boolean) {
        this.useHistory = useHistory
    }

    /*
     * 描画はバッファを書き出すのみ、バッファサイズとキャンバスのサイズが異なっていれば再構築する
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val buffer = buffer.let {
            if (it == null || it.width != width || it.height != height) {
                Bitmap.createBitmap(width, height, Config.ARGB_8888).also {
                    bufferCanvas = Canvas(it).apply {
                        drawColor(Color.WHITE)
                    }
                }.also { bitmap -> this.buffer = bitmap }
            } else it
        }
        canvas.drawBitmap(buffer, 0f, 0f, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        buffer = null
        bufferCanvas = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        bufferCanvas?.let {
            if (useHistory) {
                drawTouchWithHistory(it, event)
            } else {
                drawTouch(it, event)
            }
        }
        invalidate()
        return true
    }

    /**
     * タッチポインタを描画する。
     *
     * @param canvas 描画先
     * @param event MotionEvent
     */
    private fun drawTouch(canvas: Canvas, event: MotionEvent) {
        repeat(event.pointerCount) { i ->
            // IDに紐付いて色を分ける
            val id = event.getPointerId(i)
            paint.color = COLORS[id % COLORS.size]
            canvas.drawCircle(event.getX(i), event.getY(i), radius, paint)
        }
    }

    /**
     * タッチポインタを履歴を含めて描画する。
     *
     * @param canvas 描画先
     * @param event MotionEvent
     */
    private fun drawTouchWithHistory(canvas: Canvas, event: MotionEvent) {
        val historySize = event.historySize
        repeat(event.pointerCount) { i ->
            val id = event.getPointerId(i)
            paint.color = COLORS[id % COLORS.size]
            if (historySize == 0) {
                // 履歴がない場合はカレントを描画
                canvas.drawCircle(event.getX(i), event.getY(i), radius, paint)
            } else {
                repeat(historySize) { h ->
                    // historyは数字が小さい方が古い情報
                    canvas.drawCircle(
                        event.getHistoricalX(i, h),
                        event.getHistoricalY(i, h),
                        radius,
                        paint
                    )
                }
            }
        }
    }

    companion object {
        // 塗り分け用色
        private val COLORS = intArrayOf(
            Color.argb(255, 0, 0, 255),
            Color.argb(255, 255, 0, 255),
            Color.argb(255, 255, 0, 0),
            Color.argb(255, 255, 255, 0),
            Color.argb(255, 0, 255, 0),
            Color.argb(255, 0, 255, 255),
            Color.argb(255, 128, 0, 255),
            Color.argb(255, 255, 0, 128),
            Color.argb(255, 255, 128, 0),
            Color.argb(255, 128, 255, 0),
            Color.argb(255, 0, 255, 128),
            Color.argb(255, 0, 128, 255)
        )
    }
}
