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

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class IntroductoryView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {
    private var buffer: Bitmap? = null
    private var canvas: Canvas? = null
    private val circlePaint: Paint
    private val holePaint: Paint
    private val path: Path

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var circleRadius: Float = 0f
    private var holeRadius: Float = 0f
    private var animator: Animator? = null

    @ColorInt
    private val dimmerColor: Int = ContextCompat.getColor(context, R.color.sample2_introductory_dimmer)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sample3_introductory, this)
        circlePaint = createCirclePaint()
        holePaint = createHolePaint()
        path = Path()
        alpha = 0f
    }

    private fun createCirclePaint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.sample2_introductory_background)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        return paint
    }

    private fun createHolePaint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        return paint
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (buffer == null || this.canvas == null || buffer!!.width != canvas.width || buffer!!.height != canvas.height) {
            buffer = Bitmap.createBitmap(canvas.width, canvas.height, Config.ARGB_8888)
            this.canvas = Canvas(buffer!!)
        }
        val workCanvas = this.canvas!!
        workCanvas.drawColor(dimmerColor, PorterDuff.Mode.SRC)
        if (circleRadius == 0f) {
            canvas.drawBitmap(buffer!!, 0f, 0f, null)
            return
        }
        workCanvas.drawCircle(centerX, centerY, circleRadius, circlePaint)
        workCanvas.save()
        path.reset()
        path.addCircle(centerX, centerY, circleRadius, Direction.CW)
        workCanvas.clipPath(path)
        super.dispatchDraw(this.canvas)
        workCanvas.restore()
        if (holeRadius > 0f) {
            workCanvas.drawCircle(centerX, centerY, holeRadius, holePaint)
        }
        canvas.drawBitmap(buffer!!, 0f, 0f, null)
    }

    fun startAnimation(targetView: View) {
        val rect = Rect()
        targetView.getGlobalVisibleRect(rect)
        centerX = rect.centerX().toFloat()
        centerY = rect.centerY().toFloat()
        val targetHoleRadius = calculateDistance(rect.width().toFloat(), rect.height().toFloat()) / 2f
        setPadding(paddingLeft, (centerY + targetHoleRadius).toInt(), paddingRight, paddingBottom)
        val dimmerAnimator = createDimmerAnimator()
        val circleAnimator = createCircleAnimator()
        val holeAnimator = createHoleAnimator(targetHoleRadius)
        val animatorSet = AnimatorSet()
        animatorSet.play(dimmerAnimator).before(circleAnimator)
        animatorSet.play(circleAnimator).with(holeAnimator)
        animatorSet.start()
        animator = animatorSet
    }

    private fun createDimmerAnimator(): Animator {
        val dimmerAnimator = ValueAnimator.ofFloat(0f, 1f)
        dimmerAnimator.addUpdateListener { animation -> alpha = animation.animatedValue as Float }
        dimmerAnimator.startDelay = 200L
        dimmerAnimator.duration = 500L
        dimmerAnimator.interpolator = LinearInterpolator()
        return dimmerAnimator
    }

    private fun createCircleAnimator(): Animator {
        val radius = context.resources.getDimension(R.dimen.sample2_circle_radius)
        val circleAnimator = ValueAnimator.ofFloat(0f, radius)
        circleAnimator.interpolator = OvershootInterpolator()
        circleAnimator.startDelay = 100L
        circleAnimator.duration = 1000L
        circleAnimator.addUpdateListener { animation ->
            circleRadius = animation.animatedValue as Float
            invalidate()
        }
        return circleAnimator
    }

    private fun createHoleAnimator(radius: Float): Animator {
        val holeAnimator = ValueAnimator.ofFloat(0f, radius)
        holeAnimator.interpolator = OvershootInterpolator()
        holeAnimator.startDelay = 200L
        holeAnimator.duration = 500L
        holeAnimator.addUpdateListener { animation ->
            holeRadius = animation.animatedValue as Float
            invalidate()
        }
        return holeAnimator
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

    private fun calculateDistance(x: Float, y: Float): Float {
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
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
        canvas = null
    }
}
