/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample1

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import net.mm2d.droidkaigi2018sample.R
import net.mm2d.droidkaigi2018sample.databinding.ActivitySample1Binding

/**
 * タッチイベントを受け取りそのポインタを表示するサンプル。
 *
 * historyを表示するか否かのSwitchと、
 * 表示のクリアを行うボタンをtoolbarに配置します。
 * 処理は[Sample1View]にて実装。
 */
class Sample1Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySample1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySample1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // スイッチのon/offでhistory表示の切り替えを行う
        binding.historySwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.sample1View.setUseHistory(isChecked)
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                bottom = systemBars.bottom,
                left = systemBars.left,
                right = systemBars.right,
            )
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sample1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_clear -> binding.sample1View.clear()
            else -> return false
        }
        return true
    }
}
