/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample3

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import android.widget.Toast
import net.mm2d.droidkaigi2018sample.R

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class Sample3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample3)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val listener = { v: View -> Toast.makeText(this, (v as TextView).text, Toast.LENGTH_SHORT).show() }
        findViewById<View>(R.id.button1).setOnClickListener(listener)
        findViewById<View>(R.id.button2).setOnClickListener(listener)
        findViewById<View>(R.id.button3).setOnClickListener(listener)
        findViewById<View>(R.id.button4).setOnClickListener(listener)
        findViewById<View>(R.id.button5).setOnClickListener(listener)
        findViewById<View>(R.id.button6).setOnClickListener(listener)
        val target = findViewById<View>(R.id.button1)
        val introductoryView = IntroductoryView(this)
        (window.decorView as ViewGroup).addView(introductoryView)
        execAfterAllocateSize(target, Runnable { introductoryView.startAnimation(findViewById<View>(R.id.button1)) })
    }

    private fun execAfterAllocateSize(view: View, runnable: Runnable) {
        if (view.width == 0 || view.height == 0) {
            execOnLayout(view, runnable)
            return
        }
        runnable.run()
    }

    private fun execOnLayout(view: View, runnable: Runnable) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                runnable.run()
            }
        })
    }
}
