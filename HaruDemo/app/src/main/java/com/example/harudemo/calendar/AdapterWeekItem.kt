package com.example.harudemo.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import kotlinx.android.synthetic.main.item_week.view.*
import kotlinx.android.synthetic.main.list_item_day.view.*
import kotlinx.android.synthetic.main.my_dialog.contv
import java.util.*

class AdapterWeekItem(val tempMonth:Int, val contentlist: MutableList<String>, val size: Int): RecyclerView.Adapter<AdapterWeekItem.DayView>() {
    inner class DayView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_week, parent, false)
        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.layout.item_week.text = contentlist[position]

        holder.layout.item_week.setTextColor(Color.BLACK)
    }

    override fun getItemCount(): Int {
        return size
    }
}