/*
 * Copyright (c) 2021 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.util

/**
 * 距離計算メソッド
 */

/**
 * (x^2 + y^2)を計算する。
 */
fun hypotenuseSquare(x: Float, y: Float): Float {
    return x * x + y * y
}
