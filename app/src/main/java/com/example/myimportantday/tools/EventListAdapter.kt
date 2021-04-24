package com.example.myimportantday.tools

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myimportantday.R
import com.example.myimportantday.api.Constants
import com.squareup.picasso.Picasso


class EventListAdapter(
    private val context: Context,
    private val subjects: Array<String?>,
    private val dates: Array<String?>,
    private val places: Array<String?>,
    private val priorities: Array<String?>,
    private val advances: Array<String?>,
    private val pics: Array<String?>,
    ids: Array<String?>
) : BaseAdapter() {
    private lateinit var subject: TextView
    private lateinit var date: TextView
    private lateinit var place: TextView
    private lateinit var priority: TextView
    private lateinit var advanced: TextView
    private lateinit var pic: ImageView

    override fun getCount(): Int {
        return subjects.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val thisView = LayoutInflater.from(context).inflate(R.layout.custom_events_layout, parent, false)
        subject = thisView.findViewById(R.id.subject)
        date = thisView.findViewById(R.id.date)
        place = thisView.findViewById(R.id.place)
        priority = thisView.findViewById(R.id.priority)
        advanced = thisView.findViewById(R.id.advanced)
        pic = thisView.findViewById(R.id.pic)
        subject.text = subjects[position]
        date.text = dates[position]
        place.text = places[position]
        priority.text = priorities[position]
        advanced.text = advances[position]
        val path = Constants.BASE_URL.plus(pics[position])
        Picasso.with(context).load(path).into(pic)

        return thisView
    }
}
