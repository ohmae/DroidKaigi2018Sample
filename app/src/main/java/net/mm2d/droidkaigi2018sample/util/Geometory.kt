package net.mm2d.droidkaigi2018sample.util

import kotlin.math.hypot

/**
 * 距離計算メソッド
 */

/**
 * (x^2 + y^2)を計算する。
 */
fun hypotenuseSquare(x: Float, y: Float): Float {
    return x * x + y * y
}

/**
 * sqrt(x^2 + y^2)を計算する。
 */
fun hypotenuse(x: Float, y: Float): Float {
    return hypot(x.toDouble(), y.toDouble()).toFloat()
}
