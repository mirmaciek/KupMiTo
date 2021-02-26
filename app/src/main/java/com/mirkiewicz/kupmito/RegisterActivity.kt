package com.mirkiewicz.kupmito

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.login_field
import kotlinx.android.synthetic.main.activity_register.password_field


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener {

            if (TextUtils.isEmpty(login_field.text) || TextUtils.isEmpty(password_field.text)) {
                Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password_field.text.length < 6) {
                Toast.makeText(this, getString(R.string.password_length), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val mailregex = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
            if (!login_field.text.matches(mailregex.toRegex()))
                Toast.makeText(this, getString(R.string.provide_email), Toast.LENGTH_SHORT).show()


            val email = login_field.text.toString()
            val password = password_field.text.toString()
            val nick = name_field.text.toString()

            auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            user!!.sendEmailVerification()
                            Log.d(TAG, "createUserWithEmail:success")
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(nick).build()
                            user.updateProfile(profileUpdates);

                            Toast.makeText(baseContext, getString(R.string.register_success),
                                    Toast.LENGTH_LONG).show()

                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, getString(R.string.error_try_again),
                                    Toast.LENGTH_SHORT).show()
                        }

                    }
        }

    }

}