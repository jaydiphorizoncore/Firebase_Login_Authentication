package com.example.firebaseloginauthentication.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.firebaseloginauthentication.R
import com.example.firebaseloginauthentication.utils.Extensions.toast
import com.example.firebaseloginauthentication.utils.FirebaseUtils.firebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging


private var mFirebaseAnalytics: FirebaseAnalytics? = null

class HomeActivity : AppCompatActivity() {
    private lateinit var btnSignOut: Button
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnSignOut = findViewById(R.id.btn_sign_out)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("TokenDetails", "token failed to receive ")
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("TOKEN", token)

        })

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            toast("signed out")
            mGoogleSignInClient.signOut().addOnCompleteListener {
                startActivity(Intent(this, CreateAccountActivity::class.java))
                toast("logged Out")
                finish()
            }
        }
    }
}