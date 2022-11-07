package com.example.harudemo.grass

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.fragments.maindata
import kotlinx.android.synthetic.main.grass_item.view.*
import java.util.*

//월간 달력 어뎁터
class grassAdapter(val tempMonth: Int): RecyclerView.Adapter<grassAdapter.GrassView>() {
    inner class GrassView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrassView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.grass_item, parent, false)
        return GrassView(view)
    }

    override fun onBindViewHolder(holder: GrassView, position: Int) {
        Log.d("로그",tempMonth.toString())
        val successrate = maindata.successrate[tempMonth][position]

        if(successrate == 1) {
            // 이 FF값을 maindata.successrate의 값에 따라 조절할 예정
            // 현재 정해진 데이터가 없기 때문에 기능만 구현해 놓은 상태
            holder.layout.bglayout.setBackgroundColor(Color.parseColor("#ACE7AE"))
        }
    }

    override fun getItemCount(): Int {
        return 42
    }
}
