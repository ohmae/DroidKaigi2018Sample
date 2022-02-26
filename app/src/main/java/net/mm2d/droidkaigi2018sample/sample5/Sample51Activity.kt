/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.appcompat.app.AppCompatActivity
import net.mm2d.droidkaigi2018sample.databinding.ActivitySample5Binding

/**
 * ピンチ操作できるグリッドのサンプル。
 *
 * [GestureDetector]と[ScaleGestureDetector]を利用するバターン
 */
class Sample51Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySample5Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySample5Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpGridMap()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpGridMap() {
        val scaleDetector = ScaleGestureDetector(this, object : SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactorX = detector.currentSpanX / detector.previousSpanX
                val scaleFactorY = detector.currentSpanY / detector.previousSpanY
                binding.gridMap.gridMapContext.onScaleControl(
                    detector.focusX,
                    detector.focusY,
                    scaleFactorX,
                    scaleFactorY
                )
                binding.gridMap.invalidate()
                return true
            }
        })
        val gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                binding.gridMap.gridMapContext.onMoveControl(-distanceX, -distanceY)
                binding.gridMap.invalidate()
                return true
            }
        })
        // 2つのdetectorにeventを渡す
        binding.gridMap.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleDetector.onTouchEvent(event)
            true
        }
    }
}
