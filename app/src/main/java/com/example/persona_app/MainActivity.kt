package com.example.persona_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Para ver la variable de username
        val username = findViewById<EditText>(R.id.usernameEditText)
        val pasword = findViewById<EditText>(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.RegisterButton)


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


}