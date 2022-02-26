/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample3

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import net.mm2d.droidkaigi2018sample.databinding.ActivitySample3Binding

/**
 * オーバーレイ表示するViewにもうけた穴のタッチイベントを下のViewに伝えるサンプル。
 *
 * オーバーレイ表示しているViewのonTouchEventの戻り値で制御します。
 */
class Sample3Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySample3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySample3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // それぞれのボタンがタップされたらToastを表示する
        val listener = { v: View -> toast((v as TextView).text) }
        binding.button1.setOnClickListener(listener)
        binding.button2.setOnClickListener(listener)
        binding.button3.setOnClickListener(listener)
        binding.button4.setOnClickListener(listener)
        binding.button5.setOnClickListener(listener)
        binding.button6.setOnClickListener(listener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun onPostResume() {
        super.onPostResume()
        val introductoryView = IntroductoryView(this)
        (window.decorView as ViewGroup).addView(introductoryView)
        // button1の部分に穴が開いたオーバーレイViewを表示する。
        binding.button1.doOnLayout {
            introductoryView.startAnimation(binding.button1)
        }
    }

    private fun toast(text: CharSequence) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
