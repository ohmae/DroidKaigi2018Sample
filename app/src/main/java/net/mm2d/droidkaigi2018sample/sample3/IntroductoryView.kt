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
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import net.mm2d.droidkaigi2018sample.R
import net.mm2d.droidkaigi2018sample.util.hypotenuseSquare
import kotlin.math.sqrt

/**
 * 新機能アピール用のView
 *
 * 指定Viewが穴の中心になるように円形のViewを表示する。
 * アニメーションのところはどうでもいいので、タッチに対する対応のみ解説。
 */
class IntroductoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
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
    private var holeRadiusSquare: Float = 0f
    private var animator: Animator? = null

    @ColorInt
    private val dimmerColor: Int =
        ContextCompat.getColor(context, R.color.sample2_introductory_dimmer)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sample3_introductory, this)
        alpha = 0f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (animator?.isRunning == true) {
            // アニメーション中は下のViewに伝搬させない
            return true
        }
        if (event.action == MotionEvent.ACTION_UP ||
            event.action == MotionEvent.ACTION_CANCEL
        ) {
            // タッチの終了で非表示にする
            post {
                (parent as? ViewGroup)?.removeView(this)
            }
        }
        // 穴の範囲内の場合のみ下のViewへタッチイベントを伝搬させ
        // 「穴の中のみタッチに反応する」を実現する
        val dx = event.x - centerX
        val dy = event.y - centerY
        return hypotenuseSquare(dx, dy) >= holeRadiusSquare
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (buffer?.let { it.width == canvas.width && it.height == canvas.height } != true) {
            buffer = Bitmap.createBitmap(canvas.width, canvas.height, Config.ARGB_8888).also {
                bufferCanvas = Canvas(it)
            }
        }
        drawBuffer(bufferCanvas!!)
        canvas.drawBitmap(buffer!!, 0f, 0f, null)
    }

    /**
     * バッファへの描画を行う
     *
     * @param canvas バッファへのCanvas
     */
    private fun drawBuffer(canvas: Canvas) {
        canvas.drawColor(dimmerColor, SRC)
        if (circleRadius == 0f) {
            return
        }
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint)
        canvas.save()
        path.reset()
        path.addCircle(centerX, centerY, circleRadius, Direction.CW)
        canvas.clipPath(path)
        super.dispatchDraw(this.bufferCanvas!!)
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
        holeRadiusSquare = hypotenuseSquare(rect.width().toFloat(), rect.height().toFloat()) / 4f
        val targetHoleRadius = sqrt(holeRadiusSquare.toDouble()).toFloat()
        setPadding(paddingLeft, (centerY + targetHoleRadius).toInt(), paddingRight, paddingBottom)
        // Dimmer効果の後、円と穴のアニメーションを開始する
        animator = AnimatorSet().apply {
            val dimmerAnimator = createDimmerAnimator()
            val circleAnimator = createCircleAnimator()
            val holeAnimator = createHoleAnimator(targetHoleRadius)
            play(dimmerAnimator).before(circleAnimator)
            play(circleAnimator).with(holeAnimator)
            start()
        }
    }

    /**
     * Dimmer効果のアニメーターを作成する。
     *
     * @return アニメーター
     */
    private fun createDimmerAnimator(): Animator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener { animation -> alpha = animation.animatedValue as Float }
            startDelay = 200L
            duration = 500L
            interpolator = LinearInterpolator()
        }
    }

    /**
     * 円を描画するアニメーターを作成する。
     *
     * @return アニメーター
     */
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

    /**
     * 穴のアニメーターを作成する。
     *
     * @return アニメーター
     */
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
