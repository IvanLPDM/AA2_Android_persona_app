package com.example.persona_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.app.AlertDialog
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import android.util.Log

class MainActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Para ver la variable de username
        val username = findViewById<EditText>(R.id.usernameEditText)
        val pasword = findViewById<EditText>(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.RegisterButton)

        val googleButton: Button = findViewById(R.id.googleButton)


        registerButton.setOnClickListener {
            if (username.text.isNotEmpty() && pasword.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    username.text.toString(),
                    pasword.text.toString()).addOnCompleteListener {

                        if(it.isSuccessful)
                        {
                            val intent = Intent(this, InitActivity::class.java).apply {
                                putExtra("email", username.text.toString())
                            }
                            startActivity(intent)
                            finish()
                        }
                    else
                        {
                            showAlert()
                        }
                }
            }
        }

        loginButton.setOnClickListener {
            if (username.text.isNotEmpty() && pasword.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    username.text.toString(),
                    pasword.text.toString()).addOnCompleteListener {

                    if(it.isSuccessful)
                    {
                        val intent = Intent(this, InitActivity::class.java).apply {
                            putExtra("email", username.text.toString())
                        }
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        showAlert()
                    }
                }
            }
        }

        googleButton.setOnClickListener{

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)



        }

        session()
    }



    private fun session()
    {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email: String? = prefs.getString("email", null)

        if(email != null)
        {
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha produciudoun error de autentificación")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val intent = Intent(this, InitActivity::class.java).apply {
                                putExtra("email", account.email)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }
}