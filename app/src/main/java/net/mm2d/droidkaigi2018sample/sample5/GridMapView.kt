/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class GridMapView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr) {
    val gridMapContext: GridMapContext
    private val paint: Paint = Paint()

    init {
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        gridMapContext = GridMapContext(context)
    }

    override fun onDraw(canvas: Canvas) {
        val px = gridMapContext.x
        val py = gridMapContext.y
        val scaleX = gridMapContext.scaleX
        val scaleY = gridMapContext.scaleY
        val startX = (px.toInt() - px) * scaleX
        val startY = (py.toInt() - py) * scaleY
        var even = (px.toInt() + py.toInt()) % 2 == 0
        var y = startY
        while (y < height) {
            var work = even
            even = !even
            var x = startX
            while (x < width) {
                paint.color = if (work) Color.BLUE else Color.BLACK
                work = !work
                canvas.drawRect(x, y, x + scaleX, y + scaleY, paint)
                x += scaleX
            }
            y += scaleY
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        gridMapContext.setViewSize(width, height)
    }
}
