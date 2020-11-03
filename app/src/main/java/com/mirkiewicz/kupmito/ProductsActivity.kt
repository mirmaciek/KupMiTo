package com.mirkiewicz.kupmito

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase.getInstance
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_products.*
import kotlin.coroutines.coroutineContext


class ProductsActivity : AppCompatActivity() {

    private var newproduct: String? = null
    private val productList = ArrayList<ProductListItem>()
    private var adapter = ProductListAdapter(productList)
    private var groupID: String? = null
    private var dbRef: DatabaseReference? = getInstance().getReference("groups")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        recyclerview_products.adapter = adapter
        recyclerview_products.layoutManager = LinearLayoutManager(this)
        recyclerview_products.setHasFixedSize(true)

        groupID = intent.extras!!.getString("groupID")
        dbRef = getInstance().getReference("groups").child(groupID!!).child("products")
        readData()

        addprod_edittext.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    newproduct = addprod_edittext.text.toString().trim().replace("\n", "")
                    addProduct(newproduct!!)
                    true
                }
                else -> false
            }
        }

        addproduct_button.setOnClickListener {
            newproduct = addprod_edittext.text.toString().trim().replace("\n", "")
            addProduct(newproduct!!)
        }

        val ith = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val itemtodelete = productList[position].key
                        dbRef?.child(itemtodelete)?.removeValue()
                        val deleteditemname = productList[position].product
                        Snackbar.make(findViewById(android.R.id.content), "$deleteditemname removed", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", SnackbarUndoListener(deleteditemname)).show()
                    }
                })
        ith.attachToRecyclerView(recyclerview_products)
    }

    private fun addProduct(newproduct: String) {

        addprod_edittext.text.clear()
        if(!newproduct.equals("")) {
            dbRef!!.push().setValue(newproduct)
            readData()
        }
    }

    private fun readData() {

        dbRef?.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                productList.clear()
                adapter.notifyDataSetChanged()

                for (tmp in dataSnapshot.children) {
                    productList += ProductListItem(tmp.key.toString(),tmp.value.toString())
                    adapter.notifyItemInserted(productList.lastIndex)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ProductsActivity", "Failed to read value.", error.toException())
            }
        })
    }
    inner class SnackbarUndoListener(var item : String) : View.OnClickListener {

        override fun onClick(v: View) {
            addProduct(item)
        }
    }



}