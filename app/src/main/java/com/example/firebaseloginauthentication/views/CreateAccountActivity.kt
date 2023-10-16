package com.example.firebaseloginauthentication.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.firebaseloginauthentication.R
import com.example.firebaseloginauthentication.utils.Extensions.toast
import com.example.firebaseloginauthentication.utils.FirebaseUtils.firebaseAuth
import com.example.firebaseloginauthentication.utils.FirebaseUtils.firebaseUser
import com.google.android.gms.auth.api.identity.SavePasswordRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var edEmail: EditText
    private lateinit var edPassword: EditText
    private lateinit var edConformPassword: EditText
    private lateinit var btnCreateAccount: Button
    private lateinit var btnSignIn: Button
    private lateinit var btnSignInGoogle: Button
    private lateinit var btnCrash: Button
    private lateinit var createAccountInputsArray: Array<EditText>
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RequestCode: Int = 123

    private lateinit var userEmail: String
    private lateinit var userPassword: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        initView()
        createAccountInputsArray = arrayOf(edEmail, edPassword, edConformPassword)

        FirebaseApp.initializeApp(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        clickListener()

    }

    private fun initView() {
        edEmail = findViewById(R.id.ed_email)
        edPassword = findViewById(R.id.ed_password)
        edConformPassword = findViewById(R.id.ed_conform_password)
        btnCreateAccount = findViewById(R.id.btn_create_account)
        btnSignIn = findViewById(R.id.btn_sign_in)
        btnSignInGoogle = findViewById(R.id.btn_sign_in_google)
        btnCrash = findViewById(R.id.btn_crash)
    }

    private fun clickListener() {
        btnCreateAccount.setOnClickListener {
            signIn()
        }

        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            toast("please sign into your account")
            finish()
        }
        btnSignInGoogle.setOnClickListener {
            toast("Logging In")
            signInGoogle()
        }
        btnCrash.setOnClickListener {
            throw RuntimeException("Exception")
        }

    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RequestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }

    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }

        } catch (e: ApiException) {
            toast(e.toString())
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(
                Intent(this, HomeActivity::class.java)
            )
            toast("welcome back")
        }
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun nonEmpty(): Boolean =
        edEmail.text.toString().trim().isNotEmpty() && edPassword.text.toString().trim()
            .isNotEmpty() && edConformPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (nonEmpty() && edPassword.text.toString().trim() == edConformPassword.text.toString()
                .trim()
        ) {
            identical = true
        } else if (!nonEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        } else {
            toast("password are not matching !")
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical

            userEmail = edEmail.text.toString().trim()
            userPassword = edPassword.text.toString().trim()

            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("created account successfully !")
                        sendEmailVerification()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        toast("failed to Authentication !")
                    }
                }
        }
    }

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("email sent to $userEmail")
                }
            }
        }
    }
}