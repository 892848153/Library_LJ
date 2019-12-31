package com.lj.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lj.library.R

class SimpleAdapter(val data: List<String>): RecyclerView.Adapter<SimpleAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
//      不能使用val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_text, null)， 会导致item width 不能全屏
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
        return RecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv).text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}