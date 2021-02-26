package com.mirkiewicz.kupmito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.userslist_item.view.*

class UserListAdapter(private val list: ArrayList<String>, private val clickListener: UserListAdapter.OnItemClickListener) : RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.userslist_item, parent, false)
        return UserListViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val currentItem = list[position]
        holder.user.text = currentItem
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class UserListViewHolder(userlistView: View) : RecyclerView.ViewHolder(userlistView), View.OnClickListener {
        val user: TextView = itemView.useritemText
        val remove: ImageView = itemView.removeuser

        init {
            remove.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}