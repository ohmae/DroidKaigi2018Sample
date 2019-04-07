/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.droidkaigi2018sample

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import net.mm2d.droidkaigi2018sample.sample1.Sample1Activity
import net.mm2d.droidkaigi2018sample.sample2.Sample2Activity
import net.mm2d.droidkaigi2018sample.sample3.Sample3Activity
import net.mm2d.droidkaigi2018sample.sample4.Sample4Activity
import net.mm2d.droidkaigi2018sample.sample5.Sample51Activity
import net.mm2d.droidkaigi2018sample.sample5.Sample52Activity
import net.mm2d.log.Logger
import java.util.*

/**
 * ここから各画面を起動する。
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = ListAdapter(this, Arrays.asList(
                Link("sample1", Sample1Activity::class.java),
                Link("sample2", Sample2Activity::class.java),
                Link("sample3", Sample3Activity::class.java),
                Link("sample4", Sample4Activity::class.java),
                Link("sample5-1", Sample51Activity::class.java),
                Link("sample5-2", Sample52Activity::class.java)
        ))
    }

    private inner class Link
    internal constructor(internal val title: String, private val clazz: Class<out Activity>) {
        internal fun startActivity(context: Context) {
            try {
                context.startActivity(Intent(context, clazz))
            } catch (e: ActivityNotFoundException) {
                Logger.e(e)
            }
        }
    }

    private class ListAdapter
    internal constructor(private val context: Context, list: List<Link>)
        : Adapter<ListAdapter.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)
        private val links: List<Link> = ArrayList(list)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
                ViewHolder(inflater.inflate(R.layout.li_main_link, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.apply(links[position])
        }

        override fun getItemCount(): Int = links.size

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.text)

            fun apply(link: Link) {
                itemView.setOnClickListener { link.startActivity(context) }
                textView.text = link.title.toUpperCase()
            }
        }
    }
}
