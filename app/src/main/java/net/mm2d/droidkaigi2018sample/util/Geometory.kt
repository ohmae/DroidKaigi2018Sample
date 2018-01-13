package net.mm2d.droidkaigi2018sample.util

/**
 * 距離計算メソッド
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
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
    return Math.hypot(x.toDouble(), y.toDouble()).toFloat()
}
