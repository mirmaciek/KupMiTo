package com.mirkiewicz.kupmito

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mirkiewicz.kupmito.MainListAdapter.MainListViewHolder
import kotlinx.android.synthetic.main.mainlist_item.view.*

class MainListAdapter(private val list: List<MainListItem>) : RecyclerView.Adapter<MainListViewHolder>() {
    private val TAG: String? = "LOG MAINLISTADAPTER"
    //private val listener: OnItemClickListener

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

    class MainListViewHolder(mainlistView: View) : RecyclerView.ViewHolder(mainlistView), View.OnClickListener {
        val textviewTitle : TextView = itemView.titleText
        val textviewDesc : TextView = itemView.descriptionText
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?){
            //listener.onItemClick()

        }
    }

    interface OnItemClickListener {
        fun OnItemClick()
    }
}