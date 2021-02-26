package com.mirkiewicz.kupmito

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.login_field
import kotlinx.android.synthetic.main.activity_login.password_field
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity(),
        MailDialog.ShareDialogListener {
    val context: Context = this
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth
    var signInButton: SignInButton? = null
    private val TAG = "LoginActivity"

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
        signInButton?.setOnClickListener(View.OnClickListener { signInByGoogle() })

        standard_login_butt.setOnClickListener {

            if (TextUtils.isEmpty(login_field.text) || TextUtils.isEmpty(password_field.text)) {
                Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = login_field.text.toString()
            val password = password_field.text.toString()

            auth = FirebaseAuth.getInstance()

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            checkIfEmailVerified()
                            Log.d(TAG, "signInWithEmailAndPassword:success")
                        } else {
                            Toast.makeText(context, getString(R.string.incorrect_data), Toast.LENGTH_SHORT).show()
                        }

                    }
        }
        registerinfo.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        forgotpassword.setOnClickListener {
            val dialog = MailDialog("")
            dialog.show(supportFragmentManager, "share dialog")
        }
    }

    private fun checkIfEmailVerified() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user!!.isEmailVerified) {

            Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(context, getString(R.string.incorrect_data), Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()

        }
    }

    private fun createRequest() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInByGoogle() {
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
                firebaseAuthWithGoogle(account!!.idToken)

            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth!!.currentUser
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show()
                    }
                }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun sendMail(itemID: String, mail: String) {
        mAuth?.sendPasswordResetEmail(mail)?.addOnSuccessListener {
            Toast.makeText(applicationContext, "E-mail sent. Check your mailbox", Toast.LENGTH_SHORT).show()
        }?.addOnFailureListener {
            Toast.makeText(applicationContext, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show()
        }
    }
}