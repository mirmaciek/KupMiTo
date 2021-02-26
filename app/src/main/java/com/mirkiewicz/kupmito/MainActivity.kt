package com.mirkiewicz.kupmito

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat.*
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


class MainActivity : AppCompatActivity(),
        AddListDialog.AddListDialogListener,
        MainListAdapter.OnItemClickListener,
        MailDialog.ShareDialogListener,
        MainListAdapter.OnCreateContextMenuListener {


    private val mainList = ArrayList<MainListItem>()
    private val groupsList = ArrayList<String>()
    private val adapter = MainListAdapter(mainList, this, this)
    private val dbRef = FirebaseDatabase.getInstance().reference.child("groups")
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview_main.adapter = adapter
        recyclerview_main.layoutManager = LinearLayoutManager(this)
        recyclerview_main.setHasFixedSize(true)

        login_button.setOnClickListener {

            if (!isSignedIn()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            } else {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                mGoogleSignInClient.signOut()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))

            }
        }

        fab_addlist.setOnClickListener {
            if (isSignedIn()) {
                val dialog = AddListDialog()
                dialog.show(supportFragmentManager, "list dialog")
            } else {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.not_logged), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val signInAccount = GoogleSignIn.getLastSignedInAccount(this)

        if (isSignedIn()) {
            login_button.text = Editable.Factory.getInstance().newEditable(getString(R.string.logout))

            val displayname = FirebaseAuth.getInstance().currentUser?.displayName
            when {
                signInAccount != null -> {
                    text_welcome.text = getString(R.string.welcome_message_logged, signInAccount.givenName)
                }
                displayname != null -> text_welcome.text = getString(R.string.welcome_message_logged, displayname)
                else -> text_welcome.text = getString(R.string.welcome_message_logged_noname)
            }

            readData()
        } else {
            clearLocalData()
            login_button.text = Editable.Factory.getInstance().newEditable(getString(R.string.login))
            text_welcome.text = getString(R.string.welcome_message)
        }
    }

    private fun isSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    private fun readData() {

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearLocalData()

                for (groupnames in snapshot.children) { // k - nazwa grupy
                    for (users in groupnames.child("users").children) {
                        val mail = FirebaseAuth.getInstance().currentUser?.email.toString()

                        if (mail == users.value.toString()) {
                            groupsList += groupnames.key.toString()
                            val title = groupnames.child("title_text").value.toString()
                            val desc = groupnames.child("desc_text").value.toString()
                            mainList += MainListItem(title, desc)
                            adapter.notifyItemInserted(mainList.lastIndex)
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MainActivity", "Failed to read data from database.", error.toException())
            }
        })
    }

    fun clearLocalData() {

        groupsList.clear()
        mainList.clear()
        adapter.notifyDataSetChanged()
    }

    override fun applyTexts(name: String, description: String) {

        val mail = FirebaseAuth.getInstance().currentUser?.email.toString()
        val item = MainListItem(name, description)

        val newPostRef = dbRef.push()
        val postId = newPostRef.key
        dbRef.child(postId.toString()).setValue(item)
        dbRef.child(postId.toString()).child("users").push().setValue(mail)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        val listitem = mainList[item.order]
        val itemID = groupsList[item.order]

        when (item.itemId) {
            R.id.share_option -> {
                val dialog = MailDialog(itemID)
                dialog.show(supportFragmentManager, "share dialog")
            }
            R.id.edit_option -> {
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("name", listitem.title_text)
                intent.putExtra("desc", listitem.desc_text)
                intent.putExtra("itemID", itemID)
                startActivity(intent)
            }
            R.id.delete_option -> {
                val builder = AlertDialog.Builder(this@MainActivity)

                val message = getString(R.string.confirm_delete, listitem.title_text)
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            dbRef.child(itemID).removeValue()
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
            }
            else -> return super.onContextItemSelected(item)
        }
        return super.onContextItemSelected(item)
    }


    override fun onItemClick(position: Int) {

        val intent = Intent(this, ProductsActivity::class.java)
        val groupID = groupsList[position]
        intent.putExtra("groupID", groupID)
        startActivity(intent)
    }

    override fun sendMail(itemID: String, mail: String) {

        val auth = FirebaseAuth.getInstance()
        auth.fetchSignInMethodsForEmail(mail).addOnCompleteListener { task ->
            if (!task.result?.signInMethods.isNullOrEmpty()) {
                dbRef.child(itemID).child("users").push().setValue(mail)
                Toast.makeText(this, getString(R.string.correct_mail), Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, getString(R.string.incorrect_mail), Toast.LENGTH_SHORT).show()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onContextMenuClick(menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

    }

}