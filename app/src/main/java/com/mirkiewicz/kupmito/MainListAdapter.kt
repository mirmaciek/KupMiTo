package com.mirkiewicz.kupmito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mirkiewicz.kupmito.MainListAdapter.MainListViewHolder
import kotlinx.android.synthetic.main.mainlist_item.view.*

class MainListAdapter(private val list: List<MainListItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MainListViewHolder>() {
    private val TAG: String? = "LOG MAINLISTADAPTER"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.mainlist_item, parent, false)
        return MainListViewHolder(v)
    }

    override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {

        val currentItem = list[position]
        holder.textviewTitle.text = currentItem.title_text
        holder.textviewDesc.text = currentItem.desc_text
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MainListViewHolder(mainlistView: View) : RecyclerView.ViewHolder(mainlistView), View.OnClickListener {
        val textviewTitle : TextView = itemView.titleText
        val textviewDesc : TextView = itemView.descriptionText
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?){
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}