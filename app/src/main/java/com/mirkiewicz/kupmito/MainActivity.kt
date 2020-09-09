package com.mirkiewicz.kupmito

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AddListDialog.AddListDialogListener, MainListAdapter.OnItemClickListener {

    private val mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 123
    private val mAuth: FirebaseAuth? = null
    private var isLogged: Boolean? = false
    private var TAG: String? = "Log: MAIN"
    private val mainList = ArrayList<MainListItem>()
    private val adapter = MainListAdapter(mainList, this)

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
            text_welcome.text = getString(R.string.welcome_message_logged, username)
        }else{
            login_button.text = Editable.Factory.getInstance().newEditable(getString(R.string.login))
            text_welcome.text = getString(R.string.welcome_message)
        }
    }
    override fun applyTexts(name: String, description: String) {

        val item = MainListItem(name, description)
        mainList += item
        adapter.notifyItemInserted(mainList.lastIndex)

    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ProductsActivity::class.java)
        startActivity(intent)
    }
}