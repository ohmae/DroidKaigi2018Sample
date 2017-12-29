/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample2

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.math.MathUtils.clamp
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sample2.*
import net.mm2d.droidkaigi2018sample.R
import net.mm2d.droidkaigi2018sample.util.calculateDistance

class Sample2Activity : AppCompatActivity() {
    private var touchSlop: Int = 0
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var dragging: Boolean = false
    private var prevX: Float = 0f
    private var prevY: Float = 0f
    private var velocityX: Float = 0f
    private var velocityY: Float = 0f
    private var velocityTracker: VelocityTracker? = null
    private var animator: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample2)
        icon.setOnTouchListener { v, event -> onTouch(v, event) }
        touchSlop = ViewConfiguration.get(this).scaledTouchSlop
    }

    private fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.actionMasked
        val tracker = velocityTracker ?: VelocityTracker.obtain()
        velocityTracker = tracker
        MotionEvent.obtain(event).let {
            it.offsetLocation(v.translationX, v.translationY)
            tracker.addMovement(it)
            it.recycle()
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                stopInertialMove()
                dragging = false
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> if (dragging) {
                moveOffset(v, event.rawX - prevX, event.rawY - prevY)
            } else if (calculateDistance(event.rawX - startX, event.rawY - startY) > touchSlop) {
                dragging = true
            }
            MotionEvent.ACTION_UP -> {
                if (dragging) {
                    startInertialMove(v)
                } else {
                    Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show()
                }
                tracker.recycle()
                velocityTracker = null
            }
        }
        prevX = event.rawX
        prevY = event.rawY
        return true
    }

    private fun moveOffset(v: View, dx: Float, dy: Float) {
        val parent = v.parent as? ViewGroup ?: return
        val transitionX = v.translationX + dx
        val transitionY = v.translationY + dy
        v.translationX = clamp(transitionX, 0f, (parent.width - v.width).toFloat())
        v.translationY = clamp(transitionY, 0f, (parent.height - v.height).toFloat())
    }

    private fun startInertialMove(v: View) {
        velocityTracker?.apply {
            computeCurrentVelocity(1)
            velocityX = xVelocity * FRAME_INTERVAL
            velocityY = yVelocity * FRAME_INTERVAL
        }
        val velocity = calculateDistance(velocityX, velocityY)
        if (velocity < 1f) {
            return
        }
        val d = (Math.log((1.0 / velocity)) / Math.log(DECELERATION_RATE.toDouble()) * FRAME_INTERVAL).toLong()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = Math.max(0, d)
            addUpdateListener { _ -> inertialMove(v) }
            start()
        }
    }

    private fun stopInertialMove() {
        if (animator?.isRunning == true) {
            animator?.cancel()
        }
        animator = null
    }

    private fun inertialMove(v: View) {
        val parent = v.parent as? ViewGroup ?: return
        val transitionX = v.translationX + velocityX
        val transitionY = v.translationY + velocityY
        val maxX = (parent.width - v.width).toFloat()
        val maxY = (parent.height - v.height).toFloat()
        v.translationX = clamp(transitionX, 0f, maxX)
        v.translationY = clamp(transitionY, 0f, maxY)
        if (transitionX <= 0f || transitionX >= maxX) {
            velocityX *= -1f
        }
        if (transitionY <= 0f || transitionY >= maxY) {
            velocityY *= -1f
        }
        velocityX *= DECELERATION_RATE
        velocityY *= DECELERATION_RATE
    }

    companion object {
        private val DECELERATION_RATE = 0.95f
        private val FRAME_INTERVAL = 16f
    }
}
