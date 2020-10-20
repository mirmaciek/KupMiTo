package com.mirkiewicz.kupmito

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AddListDialog.AddListDialogListener, MainListAdapter.OnItemClickListener {


//    private val mAuth: FirebaseAuth? = null
//    private var isLogged: Boolean? = false
    private var TAG: String? = "Log: MAIN"
    private val mainList = ArrayList<MainListItem>()
    private val adapter = MainListAdapter(mainList, this)
    private val db_ref_groups = FirebaseDatabase.getInstance().reference.child("groups")
//    private val db_ref_users = FirebaseDatabase.getInstance().reference.child("users")
    private var list_title: String? = null

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
            val dialog = AddListDialog()
            dialog.show(supportFragmentManager, "list dialog")
        }

        recyclerview_main.adapter = adapter
        recyclerview_main.layoutManager = LinearLayoutManager(this)
        recyclerview_main.setHasFixedSize(true)

    }

    override fun onResume() {
        super.onResume()

        val signInAccount = GoogleSignIn.getLastSignedInAccount(this)

        if (signInAccount != null) {
            login_button.text = Editable.Factory.getInstance().newEditable(getString(R.string.logout))
            val username = signInAccount.givenName
            val id = signInAccount.id as Integer

            text_welcome.text = getString(R.string.welcome_message_logged, username)
        }else{
            login_button.text = Editable.Factory.getInstance().newEditable(getString(R.string.login))
            text_welcome.text = getString(R.string.welcome_message)
        }
    }
    override fun applyTexts(name: String, description: String) {

        val item = MainListItem(name, description)
        list_title = name;

        db_ref_groups.child(name).push().setValue(item) //dodawanie do bazy
        mainList += item
        adapter.notifyItemInserted(mainList.lastIndex)

    }

    override fun onItemClick(position: Int) {

        val intent = Intent(this, ProductsActivity::class.java)
        val item = mainList[position]
        val name = item.title_text
        intent.putExtra("Name", name)
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
        startActivity(intent)

    }

}