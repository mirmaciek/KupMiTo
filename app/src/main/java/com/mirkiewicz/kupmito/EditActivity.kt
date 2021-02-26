package com.mirkiewicz.kupmito

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_products.*


class EditActivity : AppCompatActivity(), EditDialog.EditDialogListener, UserListAdapter.OnItemClickListener {

    private val usersList = ArrayList<String>()
    private val keysList = ArrayList<String>()
    private var title : String? = ""
    private var desc : String? = ""
    private var itemID : String? = ""
    private var adapter = UserListAdapter(usersList,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        recyclerview_users.adapter = adapter
        recyclerview_users.layoutManager = LinearLayoutManager(this)
        recyclerview_users.setHasFixedSize(true)

        title = intent.extras!!.getString("name")
        desc = intent.extras!!.getString("desc")
        itemID = intent.extras!!.getString("itemID")


        tv_listname_value.text = shortenText(title!!)
        tv_listdesc_value.text = shortenText(desc!!)

        card_view_name.setOnClickListener {

            val dialog = EditDialog(title, true)
            dialog.show(supportFragmentManager, "edit dialog")

        }
        card_view_desc.setOnClickListener {
            val dialog = EditDialog(desc, false)
            dialog.show(supportFragmentManager, "edit dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        readUsers()


    }
    private fun shortenText(text : String): String {

        if (text.length > 20) {
            var shorttxt : String = text.substring(0, 20)
            shorttxt += "..."
            return shorttxt
        }
        return text
    }

    private fun readUsers(){
        FirebaseDatabase.getInstance().reference.child("groups").child(itemID!!).child("users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                keysList.clear()
                adapter.notifyDataSetChanged()
                for (user in snapshot.children) {
                    usersList += user.value.toString()
                    keysList += user.key.toString()
                    adapter.notifyItemInserted(usersList.lastIndex)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "Failed to read data from database.", error.toException())
            }
        })
    }

    override fun editText(text: String, isTitle: Boolean) {

        val dbRef = FirebaseDatabase.getInstance().reference.child("groups")
        val itemID = intent.extras!!.getString("itemID")
        var tmpText = text
        if (isTitle) {
            dbRef.child(itemID!!).child("title_text").setValue(tmpText)
            title = tmpText
            tmpText = shortenText(tmpText)
            tv_listname_value.text = tmpText
        } else {
            dbRef.child(itemID!!).child("desc_text").setValue(tmpText)
            desc = tmpText
            tmpText = shortenText(tmpText)
            tv_listdesc_value.text = tmpText

        }
    }

    override fun onItemClick(position: Int) {

        val userId = keysList[position]
        val builder = AlertDialog.Builder(this@EditActivity)

        val message = getString(R.string.confirm_delete_user)
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    usersList.removeAt(position)
                    FirebaseDatabase
                            .getInstance().reference.child("groups")
                            .child(itemID!!).child("users")
                            .child(userId).removeValue()
                    adapter.notifyItemRemoved(position)
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
        val alert = builder.create()
        alert.show()
    }
}