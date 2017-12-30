/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample.sample1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_sample1.*
import net.mm2d.droidkaigi2018sample.R

/**
 * タッチイベントを受け取りそのポインタを表示するサンプル。
 *
 * historyを表示するか否かのSwitchと、
 * 表示のクリアを行うボタンをtoolbarに配置します。
 * 処理は[Sample1View]にて実装。
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class Sample1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample1)
        setSupportActionBar(toolbar)
        historySwitch.setOnCheckedChangeListener { _, isChecked ->
            sample1View.setUseHistory(isChecked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sample1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear) {
            sample1View.clear()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
