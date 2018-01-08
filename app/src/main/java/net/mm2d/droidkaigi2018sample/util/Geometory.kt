package net.mm2d.droidkaigi2018sample.util

/**
 * 距離計算メソッド
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */

/**
 * ベクトル(x, y)の絶対値の自乗を求める。
 */
fun calculateDistanceSquare(x: Float, y: Float): Float {
    return x * x + y * y
}

/**
 * ベクトル(x, y)の絶対値を求める。
 */
fun calculateDistance(x: Float, y: Float): Float {
    return Math.sqrt((x * x + y * y).toDouble()).toFloat()
}
