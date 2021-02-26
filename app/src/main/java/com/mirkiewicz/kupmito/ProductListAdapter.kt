package com.mirkiewicz.kupmito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.productlist_item.view.*

class ProductListAdapter(private val list: List<ProductListItem>) : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.productlist_item, parent, false)
        return ProductListViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val currentItem = list[position]
        holder.product.text = currentItem.product
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ProductListViewHolder(productlistView: View) : RecyclerView.ViewHolder(productlistView) {
        val product: TextView = itemView.productText

    }

}