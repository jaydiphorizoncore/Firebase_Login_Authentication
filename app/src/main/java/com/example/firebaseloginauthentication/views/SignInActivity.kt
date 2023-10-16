package com.example.firebaseloginauthentication.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.firebaseloginauthentication.R
import com.example.firebaseloginauthentication.utils.Extensions.toast
import com.example.firebaseloginauthentication.utils.FirebaseUtils.firebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var signInEmail: String
    private lateinit var signInPassword: String
    private lateinit var signInInputsArray: Array<EditText>

    private lateinit var edSignInEmail: EditText
    private lateinit var edSignInPassword: EditText
    private lateinit var btnCreateAccount2: Button
    private lateinit var btnSignIn2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initView()
        signInInputsArray = arrayOf(edSignInEmail, edSignInPassword)
        clickListener()

    }

    private fun clickListener() {
        btnCreateAccount2.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            finish()
        }
        btnSignIn2.setOnClickListener {
            signInUser()
        }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()
    private fun signInUser() {
        signInPassword = edSignInPassword.text.toString().trim()
        signInEmail = edSignInEmail.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        toast("signed in successfully")
                        finish()
                    } else {
                        toast("SignIn failed")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }

    }

    private fun initView() {
        edSignInEmail = findViewById(R.id.edSignInEmail)
        edSignInPassword = findViewById(R.id.edSignInPassword)
        btnCreateAccount2 = findViewById(R.id.btn_create_account2)
        btnSignIn2 = findViewById(R.id.btn_signIn2)
    }
}