/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample3

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Path.Direction
import android.graphics.PorterDuff.Mode.CLEAR
import android.graphics.PorterDuff.Mode.SRC
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import net.mm2d.droidkaigi2018sample.R
import net.mm2d.droidkaigi2018sample.util.calculateDistance

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class IntroductoryView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {
    private var buffer: Bitmap? = null
    private var bufferCanvas: Canvas? = null
    private val path = Path()
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.sample2_introductory_background)
        xfermode = PorterDuffXfermode(SRC)
    }
    private val holePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        xfermode = PorterDuffXfermode(CLEAR)
    }

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var circleRadius: Float = 0f
    private var holeRadius: Float = 0f
    private var animator: Animator? = null

    @ColorInt
    private val dimmerColor: Int = ContextCompat.getColor(context, R.color.sample2_introductory_dimmer)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sample3_introductory, this)
        alpha = 0f
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (buffer == null || buffer.run { width != canvas.width || height != canvas.height }) {
            buffer = Bitmap.createBitmap(canvas.width, canvas.height, Config.ARGB_8888).also {
                bufferCanvas = Canvas(it)
            }
        }
        drawBuffer(bufferCanvas!!)
        canvas.drawBitmap(buffer!!, 0f, 0f, null)
    }

    private fun drawBuffer(canvas: Canvas) {
        canvas.drawColor(dimmerColor, PorterDuff.Mode.SRC)
        if (circleRadius == 0f) {
            return
        }
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint)
        canvas.save()
        path.reset()
        path.addCircle(centerX, centerY, circleRadius, Direction.CW)
        canvas.clipPath(path)
        super.dispatchDraw(this.bufferCanvas)
        canvas.restore()
        if (holeRadius > 0f) {
            canvas.drawCircle(centerX, centerY, holeRadius, holePaint)
        }
    }

    fun startAnimation(targetView: View) {
        val rect = Rect()
        targetView.getGlobalVisibleRect(rect)
        centerX = rect.centerX().toFloat()
        centerY = rect.centerY().toFloat()
        val targetHoleRadius = calculateDistance(rect.width().toFloat(), rect.height().toFloat()) / 2f
        setPadding(paddingLeft, (centerY + targetHoleRadius).toInt(), paddingRight, paddingBottom)
        animator = AnimatorSet().apply {
            val dimmerAnimator = createDimmerAnimator()
            val circleAnimator = createCircleAnimator()
            val holeAnimator = createHoleAnimator(targetHoleRadius)
            play(dimmerAnimator).before(circleAnimator)
            play(circleAnimator).with(holeAnimator)
            start()
        }
    }

    private fun createDimmerAnimator(): Animator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener { animation -> alpha = animation.animatedValue as Float }
            startDelay = 200L
            duration = 500L
            interpolator = LinearInterpolator()
        }
    }

    private fun createCircleAnimator(): Animator {
        val radius = context.resources.getDimension(R.dimen.sample2_circle_radius)
        return ValueAnimator.ofFloat(0f, radius).apply {
            interpolator = OvershootInterpolator()
            startDelay = 100L
            duration = 1000L
            addUpdateListener { animation ->
                circleRadius = animation.animatedValue as Float
                invalidate()
            }
        }
    }

    private fun createHoleAnimator(targetRadius: Float): Animator {
        return ValueAnimator.ofFloat(0f, targetRadius).apply {
            interpolator = OvershootInterpolator()
            startDelay = 200L
            duration = 500L
            addUpdateListener { animation ->
                holeRadius = animation.animatedValue as Float
                invalidate()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (animator?.isRunning == true) {
            return true
        }
        if (event.action == MotionEvent.ACTION_UP) {
            val parent = parent as? ViewGroup
            parent?.removeView(this)
        }
        val dx = event.x - centerX
        val dy = event.y - centerY
        return calculateDistance(dx, dy) >= holeRadius
    }

    private fun stopAnimation() {
        if (animator?.isRunning == true) {
            animator?.cancel()
        }
        animator = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
        buffer = null
        bufferCanvas = null
    }
}
