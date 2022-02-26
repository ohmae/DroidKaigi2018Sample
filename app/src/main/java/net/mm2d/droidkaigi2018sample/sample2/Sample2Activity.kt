/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample2

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.math.MathUtils.clamp
import net.mm2d.droidkaigi2018sample.databinding.ActivitySample2Binding
import net.mm2d.droidkaigi2018sample.util.hypotenuseSquare
import kotlin.math.hypot
import kotlin.math.ln

/**
 * タッチイベントを受け取りViewの移動を行うサンプル。
 *
 * ここでは、Viewを継承してonTouchEventをoverrideするのではなく、OnTouchListenerを使用しています。
 * onTouchEventとOnTouchListener#onTouchがコールされる条件はほぼ同等で、
 * OnTouchListenerが設定されている場合、OnTouchListenerが先にコールされます。
 * OnTouchListenerが設定されていない場合、もしくはOnTouchListener#onTouchがfalseを返した場合に、
 * onTouchEventがコールされます。
 */
class Sample2Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySample2Binding
    private val touchSlopSquare by lazy {
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        touchSlop * touchSlop
    }
    private var startX = 0f
    private var startY = 0f
    private var dragging = false
    private var prevX = 0f
    private var prevY = 0f
    private var velocityX = 0f
    private var velocityY = 0f
    private var velocityTracker: VelocityTracker? = null
    private var animator: Animator? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySample2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.icon.setOnTouchListener(::onTouch)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    /**
     * ViewのonTouch処理。
     *
     * @param view  対象View
     * @param event MotionEvent
     */
    private fun onTouch(view: View, event: MotionEvent): Boolean {
        // VelocityTrackerのインスタンスを使い回すための処理
        val tracker = velocityTracker ?: VelocityTracker.obtain()
        velocityTracker = tracker
        MotionEvent.obtain(event).let {
            // Viewがタッチに反応して移動するため
            // VelocityTrackerにはMotionEventをそのまま渡すと正しい速度が計算できません。
            // copyを作成し、座標の補正を行った上でVelocityTrackerに渡します。
            // 直接offsetLocationで座標の補正を行ってもよいですが
            // その場合、処理を抜ける前に座標を戻しておかないとこの先のイベントの伝搬すべてに影響が及んでしまいます。
            it.offsetLocation(event.rawX - event.x, event.rawY - event.y)
            tracker.addMovement(it)
            it.recycle()
        }
        // 無駄にインスタンスを作らないパターン
        // val dx = event.rawX - event.x
        // val dy = event.rawY - event.y
        // event.offsetLocation(dx, dy)
        // tracker.addMovement(event)
        // event.offsetLocation(-dx, -dy)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                stopInertialMove()
                dragging = false
                startX = event.rawX
                startY = event.rawY
            }
            MotionEvent.ACTION_MOVE ->
                // 移動距離がtouchSlopを超えるまでドラッグ動作を行わない
                if (dragging) {
                    moveOffset(view, event.rawX - prevX, event.rawY - prevY)
                } else if (hypotenuseSquare(
                        event.rawX - startX,
                        event.rawY - startY
                    ) > touchSlopSquare
                ) {
                    dragging = true
                }
            MotionEvent.ACTION_UP -> {
                if (dragging) {
                    startInertialMove(view)
                } else {
                    // ドラッグが発生する前に指が離れたらタップと判定する
                    Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show()
                }
                tracker.recycle()
                velocityTracker = null
            }
            MotionEvent.ACTION_CANCEL -> {
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

    /**
     * タッチが終了した後それまでの移動速度に応じた慣性アニメーションを開始。
     *
     * @param view 対象View
     */
    private fun startInertialMove(view: View) {
        // 1フレームあたりの移動距離を計算する
        velocityTracker?.run {
            computeCurrentVelocity(FRAME_INTERVAL)
            velocityX = xVelocity
            velocityY = yVelocity
        }
        // 速度の絶対値を求める
        val velocity = hypot(velocityX, velocityY)
        if (velocity < 1f) {
            return
        }
        // 移動速度が1を下回るまでの時間を計算する
        val assumedDuration =
            (ln((1.0 / velocity)) / ln(DECELERATION_RATE.toDouble()) * FRAME_INTERVAL).toLong()
        if (assumedDuration <= 0) {
            return
        }
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = assumedDuration
            addUpdateListener { inertialMove(view) }
            start()
        }
    }

    /**
     * 慣性移動の停止。
     */
    private fun stopInertialMove() {
        if (animator?.isRunning == true) {
            animator?.cancel()
        }
        animator = null
    }

    /**
     * 慣性移動を行う。
     *
     * @param view 対象のView
     */
    private fun inertialMove(view: View) {
        val parent = view.parent as? ViewGroup ?: return
        val transitionX = view.translationX + velocityX
        val transitionY = view.translationY + velocityY
        val maxX = (parent.width - view.width).toFloat()
        val maxY = (parent.height - view.height).toFloat()
        // 画面からはみ出さない範囲に移動
        view.translationX = clamp(transitionX, 0f, maxX)
        view.translationY = clamp(transitionY, 0f, maxY)
        // 画面端まで移動したら速度を反転させる
        if (transitionX <= 0f || transitionX >= maxX) {
            velocityX *= -1f
        }
        if (transitionY <= 0f || transitionY >= maxY) {
            velocityY *= -1f
        }
        // 速度を減衰させる
        velocityX *= DECELERATION_RATE
        velocityY *= DECELERATION_RATE
    }

    companion object {
        // 慣性移動の減衰率
        private const val DECELERATION_RATE = 0.95f

        // 1フレームの時間、16msとしておく
        private const val FRAME_INTERVAL = 16
    }
}
