/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample4

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sample4.*
import net.mm2d.droidkaigi2018sample.R

/**
 * Sample2に似ているが、ドラッグによる移動をViewGroupで制御し、子Viewではタップの判定を行うサンプル。
 *
 * [ScrollLayout]をご覧ください。
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class Sample4Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample4)
        setSupportActionBar(toolbar)
        // 子ViewのOnClickListenerでクリック判定を行う
        icon.setOnClickListener {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
