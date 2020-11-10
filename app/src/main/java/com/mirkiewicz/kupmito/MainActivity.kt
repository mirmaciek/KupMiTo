package com.mirkiewicz.kupmito

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AddListDialog.AddListDialogListener, MainListAdapter.OnItemClickListener, MainListAdapter.OnCreateContextMenuListener {


    private val mainList = ArrayList<MainListItem>()
    private val groupsList = ArrayList<String>()
//    private val mainMap = LinkedHashMap<String,MainListItem>()
    private val adapter = MainListAdapter(mainList, this, this)
    private val db_ref = FirebaseDatabase.getInstance().reference.child("groups")
//    private val db_ref_users = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_button.setOnClickListener {

            if(login_button.text.toString() == getString(R.string.login)){

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            }else if(login_button.text.toString() == getString(R.string.logout)){

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                mGoogleSignInClient.signOut()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))

            }
        }

        fab_addlist.setOnClickListener{
            if (FirebaseAuth.getInstance().currentUser != null){
                val dialog = AddListDialog(null)
                dialog.show(supportFragmentManager, "list dialog")
            }else{
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.not_logged), Snackbar.LENGTH_LONG).show()
            }

        }

        recyclerview_main.adapter = adapter
        recyclerview_main.layoutManager = LinearLayoutManager(this)
        recyclerview_main.setHasFixedSize(true)
        readData()
    }

    override fun onResume() {
        super.onResume()

        val signInAccount = GoogleSignIn.getLastSignedInAccount(this)

        if (signInAccount != null) {
            login_button.text = Editable.Factory.getInstance().newEditable(getString(R.string.logout))
            val username = signInAccount.givenName
//            val id = String().toInt(signInAccount.id)

            text_welcome.text = getString(R.string.welcome_message_logged, username)
//            readData()
        }else{
            clearLocalData()
            login_button.text = Editable.Factory.getInstance().newEditable(getString(R.string.login))
            text_welcome.text = getString(R.string.welcome_message)
        }

    }
    private fun readData() {

        Toast.makeText(this, "zczytywanko", Toast.LENGTH_SHORT).show()
        db_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearLocalData()

                for (groupnames in snapshot.children) { // k - nazwa grupy

                    groupsList += groupnames.key.toString()
                    val title = groupnames.child("title_text").value.toString()
                    val desc = groupnames.child("desc_text").value.toString()
                    mainList += MainListItem(title, desc)
                    adapter.notifyItemInserted(mainList.lastIndex)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "Failed to read data from database.", error.toException())
            }
        })
    }

    override fun applyTexts(name: String, description: String, id: String?) {

        if (id != null){
            db_ref.child(id).child("title_text").setValue(name)
            db_ref.child(id).child("desc_text").setValue(description)

            Toast.makeText(this, "successfully edited", Toast.LENGTH_SHORT).show()
        }else{
            val item = MainListItem(name, description)
            db_ref.push().setValue(item)
        }
//        readData()

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        val listitem = mainList[item.order]
        val itemID = groupsList[item.order]

        when(item.itemId){
            R.id.share_option -> Toast.makeText(this, "TBD", Toast.LENGTH_SHORT).show()
            R.id.edit_option -> {
                val dialog = AddListDialog(itemID)
                dialog.show(supportFragmentManager, "list dialog")
            }
            R.id.delete_option -> {
                val builder = AlertDialog.Builder(this@MainActivity)

                val message = getString(R.string.confirm_delete, listitem.title_text)
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            db_ref.child(itemID).removeValue()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
            }
            else -> return super.onContextItemSelected(item)
        }
        return super.onContextItemSelected(item)
    }

    fun clearLocalData(){

        groupsList.clear()
        mainList.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {

        val intent = Intent(this, ProductsActivity::class.java)
        val groupID = groupsList[position]
        intent.putExtra("groupID", groupID)
        startActivity(intent)
    }

    override fun onContextMenuClick(menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

    }

}