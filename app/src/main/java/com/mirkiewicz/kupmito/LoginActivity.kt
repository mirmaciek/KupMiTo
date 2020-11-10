package com.mirkiewicz.kupmito

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import android.content.Intent
import com.mirkiewicz.kupmito.MainActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mirkiewicz.kupmito.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mirkiewicz.kupmito.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    val context: Context = this
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    var signInButton: SignInButton? = null
    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user != null) {
            val toast = Toast.makeText(applicationContext, "Błąd, użytkownik wciąż zalogowany", Toast.LENGTH_SHORT)
            toast.show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        createRequest()
        signInButton = findViewById(R.id.login_butt)
        signInButton?.setSize(SignInButton.SIZE_WIDE)
        signInButton?.setOnClickListener(View.OnClickListener { signIn() })
    }

    private fun createRequest() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //                TBD CZEMU NIE DZIALA DEFAULT?????????
                //                .requestIdToken("273479523282-667fotp04iq25os7f6hvfgu6m4086dbt.apps.googleusercontent.com")
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                //                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account!!.idToken)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth!!.currentUser
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        //                            updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        val toast = Toast.makeText(applicationContext, "Błąd logowania, spróbuj ponownie", Toast.LENGTH_SHORT)
                        toast.show()
                        //                            updateUI(null);
                    }
                }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}