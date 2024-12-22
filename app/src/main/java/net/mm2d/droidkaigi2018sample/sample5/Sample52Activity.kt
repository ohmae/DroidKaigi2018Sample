/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import net.mm2d.droidkaigi2018sample.databinding.ActivitySample5Binding
import net.mm2d.droidkaigi2018sample.sample5.MultiTouchGestureDetector.GestureListener

/**
 * ピンチ操作できるグリッドのサンプル。
 *
 * [MultiTouchGestureDetector]を利用するバターン
 */
class Sample52Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySample5Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySample5Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpGridMap()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                bottom = systemBars.bottom,
                left = systemBars.left,
                right = systemBars.right,
            )
            insets
        }
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
        val detector = MultiTouchGestureDetector(this, object : GestureListener {
            override fun onMove(@Dimension deltaX: Float, @Dimension deltaY: Float) {
                binding.gridMap.gridMapContext.onMoveControl(deltaX, deltaY)
                binding.gridMap.invalidate()
            }

            override fun onScale(
                @Dimension focusX: Float,
                @Dimension focusY: Float,
                scaleFactorX: Float,
                scaleFactorY: Float
            ) {
                binding.gridMap.gridMapContext
                    .onScaleControl(focusX, focusY, scaleFactorX, scaleFactorY)
                binding.gridMap.invalidate()
            }
        })
        binding.gridMap.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            true
        }
    }
}
