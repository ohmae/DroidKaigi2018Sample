package net.mm2d.droidkaigi2018sample.util

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
fun calculateDistance(x: Float, y: Float): Float {
    return Math.sqrt((x * x + y * y).toDouble()).toFloat()
}
