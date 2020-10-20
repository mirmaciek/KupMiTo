package com.mirkiewicz.kupmito

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : AppCompatActivity(), ProductListAdapter.OnItemClickListener {

    private var newproduct: String? = null
    private val productList = ArrayList<ProductListItem>()
    private val adapter = ProductListAdapter(productList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        recyclerview_products.adapter = adapter
        recyclerview_products.layoutManager = LinearLayoutManager(this)
        recyclerview_products.setHasFixedSize(true)

        addproduct_button.setOnClickListener {
            newproduct = addprod_edittext.text.toString()
            addprod_edittext.text.clear()
            if(!newproduct.equals("")) {


                val bundle = intent.extras
                val name: String? = bundle!!.getString("Name")
                val db_ref = FirebaseDatabase.getInstance().getReference("groups").child(name!!)

                db_ref.child("products").push().setValue(newproduct)
                
                val item = ProductListItem(newproduct!!)
                productList+=item
                adapter.notifyItemInserted(productList.lastIndex)
            }
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
    }
}