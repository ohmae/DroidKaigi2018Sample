/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample

import android.app.Application

import net.mm2d.log.AndroidLogInitializer
import net.mm2d.log.Log

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.setInitializer(AndroidLogInitializer.get())
        Log.initialize(BuildConfig.DEBUG, true)
    }
}
