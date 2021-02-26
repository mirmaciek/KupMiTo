package com.mirkiewicz.kupmito

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        delete.setOnClickListener {

            if (FirebaseAuth.getInstance().currentUser != null) {
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)

                val message = getString(R.string.confirm_delete_account)
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->

                            deleteUserFromLists()

                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
            } else
                Toast.makeText(applicationContext, getString(R.string.not_logged), Toast.LENGTH_SHORT).show()


        }
        about.setOnClickListener {

            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            val message = getString(R.string.about_content)
            builder.setMessage(message)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(getString(R.string.about))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok)) { _, _ -> }

            val alert = builder.create()
            alert.show()
        }


    }

    private fun deleteUserFromLists() {

        val user = FirebaseAuth.getInstance().currentUser
        val mail = FirebaseAuth.getInstance().currentUser?.email.toString()

        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dbRef = FirebaseDatabase.getInstance().reference.child("groups")
                dbRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (groupnames in snapshot.children) {

                            for (users in groupnames.child("users").children) {


                                if (mail == users.value.toString())
                                    users.ref.removeValue()
                            }
                            if (!groupnames.child("users").hasChildren()) {
                                groupnames.ref.removeValue()
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read data from database.", error.toException())
                    }
                })
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                mGoogleSignInClient.signOut()
                FirebaseAuth.getInstance().signOut()

                Toast.makeText(applicationContext, getString(R.string.account_deleted), Toast.LENGTH_SHORT).show()
                finish()

            }
        }
    }
}