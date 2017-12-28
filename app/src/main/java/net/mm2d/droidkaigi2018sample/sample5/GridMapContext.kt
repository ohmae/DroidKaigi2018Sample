/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.content.Context

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class GridMapContext internal constructor(context: Context) {
    private val scaleXMin: Float
    private val scaleYMin: Float
    private val scaleXMax: Float
    private val scaleYMax: Float

    internal var scaleX = 100f
        private set
    internal var scaleY = 100f
        private set
    internal var x = 0f
        private set
    internal var y = 0f
        private set
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    init {
        val density = context.resources.displayMetrics.density
        scaleX = 100f * density
        scaleY = 100f * density
        scaleXMin = 50f * density
        scaleYMin = 50f * density
        scaleXMax = 1000f * density
        scaleYMax = 1000f * density
    }

    fun setViewSize(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
        ensureGridRange()
    }

    internal fun onMoveControl(deltaX: Float, deltaY: Float) {
        x -= deltaX / scaleX
        y -= deltaY / scaleY
        ensureGridRange()
    }

    internal fun onScaleControl(focusX: Float, focusY: Float, scaleX: Float, scaleY: Float) {
        val newScaleX = clamp(this.scaleX * scaleX, scaleXMin, scaleXMax)
        val newScaleY = clamp(this.scaleY * scaleY, scaleYMin, scaleYMax)
        x -= focusX / newScaleX - focusX / this.scaleX
        y -= focusY / newScaleY - focusY / this.scaleY
        this.scaleX = newScaleX
        this.scaleY = newScaleY
        ensureGridRange()
    }

    private fun ensureGridRange() {
        x = clamp(x, 0f, GRID_X - viewWidth / scaleX)
        y = clamp(y, 0f, GRID_Y - viewHeight / scaleY)
    }

    companion object {
        private val GRID_X = 400
        private val GRID_Y = 400

        private fun clamp(value: Float, min: Float, max: Float): Float {
            return Math.min(Math.max(value, min), max)
        }
    }
}
