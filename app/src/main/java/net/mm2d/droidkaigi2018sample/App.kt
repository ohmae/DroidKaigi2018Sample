/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample

import android.app.Application
import net.mm2d.log.Logger
import net.mm2d.log.android.AndroidSenders

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Logger.setSender(AndroidSenders.create())
            Logger.setLogLevel(Logger.VERBOSE)
        }
    }
}
