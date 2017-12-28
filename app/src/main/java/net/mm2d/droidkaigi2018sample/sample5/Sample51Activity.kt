/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener

import net.mm2d.droidkaigi2018sample.R

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class Sample51Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample5)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        setUpGridMap()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpGridMap() {
        val gridMapView = findViewById<GridMapView>(R.id.grid_map)
        val scaleDetector = ScaleGestureDetector(this, object : SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleX = detector.currentSpanX / detector.previousSpanX
                val scaleY = detector.currentSpanY / detector.previousSpanY
                gridMapView.gridMapContext.onScaleControl(detector.focusX, detector.focusY, scaleX, scaleY)
                gridMapView.invalidate()
                return true
            }
        })
        val gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                gridMapView.gridMapContext.onMoveControl(-distanceX, -distanceY)
                gridMapView.invalidate()
                return true
            }
        })
        gridMapView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleDetector.onTouchEvent(event)
            true
        }
    }
}
