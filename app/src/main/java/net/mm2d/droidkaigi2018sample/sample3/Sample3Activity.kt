/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample3

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sample3.*
import net.mm2d.droidkaigi2018sample.R

/**
 * オーバーレイ表示するViewにもうけた穴のタッチイベントを下のViewに伝えるサンプル。
 *
 * オーバーレイ表示しているViewのonTouchEventの戻り値で制御します。
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class Sample3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample3)
        setSupportActionBar(toolbar)
        // それぞれのボタンがタップされたらToastを表示する
        val listener = { v: View -> toast((v as TextView).text) }
        button1.setOnClickListener(listener)
        button2.setOnClickListener(listener)
        button3.setOnClickListener(listener)
        button4.setOnClickListener(listener)
        button5.setOnClickListener(listener)
        button6.setOnClickListener(listener)
    }

    override fun onPostResume() {
        super.onPostResume()
        val introductoryView = IntroductoryView(this)
        (window.decorView as ViewGroup).addView(introductoryView)
        // button1の部分に穴が開いたオーバーレイViewを表示する。
        execAfterAllocateSize(button1, { introductoryView.startAnimation(button1) })
    }

    private fun toast(text: CharSequence) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    /**
     * viewにサイズが割り当てられた後で実行する。
     *
     * @param view     監視対象のView
     * @param function 実行する処理
     */
    private fun execAfterAllocateSize(view: View, function: () -> Unit) {
        if (view.width == 0 || view.height == 0) {
            execOnLayout(view, function)
            return
        }
        function()
    }

    /**
     * GlobalLayoutの後に一回のみ実行する。
     *
     * @param view     監視対象のView
     * @param function 実行する処理
     */
    private fun execOnLayout(view: View, function: () -> Unit) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                function()
            }
        })
    }
}
