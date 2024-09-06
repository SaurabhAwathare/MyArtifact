package com.saurabhawathare.myartifact03

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var googleLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize GoogleSignInOptions
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Initialize GoogleSignInClient
        gsc = GoogleSignIn.getClient(this, gso)

        // Initialize googleLogo
        googleLogo = findViewById(R.id.googlelogo)

        // Set onClickListener for the Google sign-in button
        googleLogo.setOnClickListener {
            val signInIntent = gsc.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Check if user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            navigateToSecondActivity(account)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    navigateToSecondActivity(account)
                } else {
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                handleSignInError(e.statusCode)
            }
        }
    }

    private fun navigateToSecondActivity(account: GoogleSignInAccount) {
        // Handle navigation to the second activity with the signed-in account
        // Passing account information via intent
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra("accountName", account.displayName)
            putExtra("accountEmail", account.email)
            putExtra("accountPhotoUrl", account.photoUrl.toString())
        }
        startActivity(intent)
    }

    private fun handleSignInError(statusCode: Int) {
        when (statusCode) {
            GoogleSignInStatusCodes.NETWORK_ERROR ->
                Toast.makeText(this, "Network error, please try again", Toast.LENGTH_SHORT).show()
            GoogleSignInStatusCodes.SIGN_IN_CANCELLED ->
                Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
            GoogleSignInStatusCodes.SIGN_IN_FAILED ->
                Toast.makeText(this, "Sign-in failed, please try again", Toast.LENGTH_SHORT).show()
            else ->
                Toast.makeText(this, "Error code: $statusCode", Toast.LENGTH_SHORT).show()
        }
    }
}
