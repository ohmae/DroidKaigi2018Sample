/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample5

import android.content.Context
import androidx.core.math.MathUtils.clamp

/**
 * グリッド表示のコンテキスト。
 *
 * 1グリッドを1としたマップがあり、
 * その上の表示領域を示す窓の位置と拡大率を保持することで表現する。
 * 1グリッドが1であるため、拡大率は実際に表示されるグリッドの長さ(pixel)に対応する。
 */
class GridMapContext internal constructor(context: Context) {
    /**
     * X軸方向の拡大率最小値
     */
    private val scaleXMin: Float
    /**
     * Y軸方向の拡大率最小値
     */
    private val scaleYMin: Float
    /**
     * X軸方向の拡大率最大値
     */
    private val scaleXMax: Float
    /**
     * Y軸方向の拡大率最大値
     */
    private val scaleYMax: Float

    /**
     * X座標
     */
    internal var x = 40f
        private set
    /**
     * Y座標
     */
    internal var y = 40f
        private set
    /**
     * X軸方向の拡大率
     */
    internal var scaleX = 100f
        private set
    /**
     * Y軸方向の拡大率
     */
    internal var scaleY = 100f
        private set
    /**
     * Viewの幅
     */
    private var viewWidth = 0
    /**
     * Viewの高さ
     */
    private var viewHeight = 0

    init {
        val density = context.resources.displayMetrics.density
        scaleX = 100f * density
        scaleY = 100f * density
        scaleXMin = 50f * density
        scaleYMin = 50f * density
        scaleXMax = 1000f * density
        scaleYMax = 1000f * density
    }

    /**
     * Viewのサイズを設定する。
     */
    fun setViewSize(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
        ensureGridRange()
    }

    /**
     * 移動制御に伴う座標の変更を行う。
     *
     * @param deltaX X軸方向のグリッドの移動量
     * @param deltaY Y軸方向のグリッドの移動量
     */
    internal fun onMoveControl(deltaX: Float, deltaY: Float) {
        // 引数は表示されているグリッドの移動量であるため
        // 左上の座標の移動量は逆方向に拡大率で割った値となる。
        x -= deltaX / scaleX
        y -= deltaY / scaleY
        ensureGridRange()
    }

    /**
     * 拡大率の変更を行う。
     *
     * @param focusX ピンチ操作の中心座標
     * @param focusY ピンチ操作の中心座標
     * @param scaleFactorX X軸方向の拡大率の変化
     * @param scaleFactorY Y軸方向の拡大率の変化
     */
    internal fun onScaleControl(
        focusX: Float,
        focusY: Float,
        scaleFactorX: Float,
        scaleFactorY: Float
    ) {
        // 変更後の拡大率が最小値から最大値の範囲内に収まるように補正を行う
        val newScaleX = clamp(scaleX * scaleFactorX, scaleXMin, scaleXMax)
        val newScaleY = clamp(scaleY * scaleFactorY, scaleYMin, scaleYMax)
        // 操作の中心座標までの距離の拡大率の変化に伴う変化分を移動させる
        x -= focusX / newScaleX - focusX / scaleX
        y -= focusY / newScaleY - focusY / scaleY
        scaleX = newScaleX
        scaleY = newScaleY
        ensureGridRange()
    }

    /**
     * 表示エリアがマップの範囲内に収まるように補正を行う。
     */
    private fun ensureGridRange() {
        x = clamp(x, 0f, GRID_X - viewWidth / scaleX)
        y = clamp(y, 0f, GRID_Y - viewHeight / scaleY)
    }

    companion object {
        /**
         * X軸方向のグリッド数
         */
        private const val GRID_X = 400
        /**
         * Y軸方向のグリッド数
         */
        private const val GRID_Y = 400
    }
}
