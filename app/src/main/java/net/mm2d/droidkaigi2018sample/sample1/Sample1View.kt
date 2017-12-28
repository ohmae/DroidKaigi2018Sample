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
import android.support.annotation.Dimension
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import net.mm2d.droidkaigi2018sample.R

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class Sample1View
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {
    @Dimension
    private val radius: Float
    private val paint: Paint
    private var buffer: Bitmap? = null
    private var canvas: Canvas? = null

    private var mUseHistory: Boolean = false

    init {
        val resources = context.resources
        radius = resources.getDimension(R.dimen.sample1_radius)
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Style.STROKE
        paint.strokeWidth = resources.getDimension(R.dimen.sample1_stroke_width)
    }

    fun clear() {
        canvas?.drawColor(Color.WHITE) ?: return
        invalidate()
    }

    fun setUseHistory(useHistory: Boolean) {
        mUseHistory = useHistory
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (canvas == null) {
            return true
        }
        val pinterCount = event.pointerCount
        if (mUseHistory) {
            val historySize = event.historySize
            for (p in 0 until pinterCount) {
                val index = event.getPointerId(p)
                paint.color = COLORS[index % COLORS.size]
                for (h in 0 until historySize) {
                    canvas!!.drawCircle(event.getHistoricalX(p, h), event.getHistoricalY(p, h), radius, paint)
                }
            }
        } else {
            for (p in 0 until pinterCount) {
                val index = event.getPointerId(p)
                paint.color = COLORS[index % COLORS.size]
                canvas!!.drawCircle(event.getX(p), event.getY(p), radius, paint)
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (buffer == null || buffer?.width != canvas.width || buffer?.height != canvas.height) {
            buffer = Bitmap.createBitmap(canvas.width, canvas.height, Config.ARGB_8888)
            this.canvas = Canvas(buffer!!)
            this.canvas!!.drawColor(Color.WHITE)
        }
        canvas.drawBitmap(buffer!!, 0f, 0f, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        buffer = null
        canvas = null
    }

    companion object {
        private val COLORS = intArrayOf(
                Color.argb(255, 255, 128, 128),
                Color.argb(255, 255, 255, 128),
                Color.argb(255, 128, 255, 128),
                Color.argb(255, 128, 255, 255),
                Color.argb(255, 128, 128, 255),
                Color.argb(255, 255, 128, 255),
                Color.argb(255, 255, 192, 128),
                Color.argb(255, 192, 255, 128),
                Color.argb(255, 128, 255, 192),
                Color.argb(255, 128, 192, 255),
                Color.argb(255, 192, 128, 255),
                Color.argb(255, 255, 128, 192)
        )
    }
}
