package com.example.persona_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.app.AlertDialog
import android.content.res.Configuration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import android.util.Log
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private lateinit var firebaseAnalytics: FirebaseAnalytics



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val backgroundImage: ImageFilterView = findViewById(R.id.backgroundmain)

        // Obtener el estado del tema guardado en SharedPreferences
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Aplicar el tema correcto
        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, theme))
        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, theme))
        }


        // Verificar si el usuario ya está autenticado
        session()

        //Para ver la variable de username
        val username = findViewById<EditText>(R.id.usernameEditText)
        val pasword = findViewById<EditText>(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.RegisterButton)

        val googleButton: Button = findViewById(R.id.googleButton)



        firebaseAnalytics = FirebaseAnalytics.getInstance(this)




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
                            logLoginEvent("email")
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
            if (username.text.isNotEmpty() && pasword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    username.text.toString(),
                    pasword.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, InitActivity::class.java).apply {
                            putExtra("email", username.text.toString())
                        }
                        logLoginEvent("email")
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("LoginError", "Authentication failed: ${task.exception?.message}")
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

    private fun session() {
        // Verificar si el usuario está autenticado con Firebase Auth
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {

            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            finish()
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
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                val intent = Intent(this, InitActivity::class.java).apply {
                                    putExtra("email", account.email)
                                }
                                logLoginEvent("google")
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
    // Registrar eventos en Analytics (firebase)
    fun logLoginEvent(method: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, method)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }
}