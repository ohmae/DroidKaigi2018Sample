/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.Dimension
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sample5.*
import net.mm2d.droidkaigi2018sample.R
import net.mm2d.droidkaigi2018sample.sample5.MultiTouchGestureDetector.GestureListener

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class Sample52Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample5)
        setSupportActionBar(toolbar)
        setUpGridMap()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpGridMap() {
        val detector = MultiTouchGestureDetector(this, object : GestureListener {
            override fun onMove(@Dimension deltaX: Float, @Dimension deltaY: Float) {
                gridMap.gridMapContext.onMoveControl(deltaX, deltaY)
                gridMap.invalidate()
            }

            override fun onScale(@Dimension focusX: Float, @Dimension focusY: Float, scaleX: Float, scaleY: Float) {
                gridMap.gridMapContext.onScaleControl(focusX, focusY, scaleX, scaleY)
                gridMap.invalidate()
            }
        })
        gridMap.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            true
        }
    }
}
