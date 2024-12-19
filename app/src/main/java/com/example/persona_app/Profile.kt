package com.example.persona_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Selector
            val showImageButton: Button = findViewById(R.id.menuOpen)
            val hiddenImageButton: Button = findViewById(R.id.menuClose)
            val hiddenImageZone: Button = findViewById(R.id.CloseZone)
            val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)

            val newsButton: Button = findViewById(R.id.news_button)
            val profileButton: Button = findViewById(R.id.Profile_Button)

            val saveButton: Button = findViewById(R.id.SaveButton)
            val getButton: Button = findViewById(R.id.GetButton)
            val deleteButton: Button = findViewById(R.id.DeleteButton)

            val usernameText: EditText = findViewById(R.id.usernameText)
            val phoneText: EditText = findViewById(R.id.PhoneText)

        //Base de datos
            val db = FirebaseFirestore.getInstance()

            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email.toString()
            val provider = user?.providerId

            showImageButton.setOnClickListener {
                sceneSelectorLayout.visibility = View.VISIBLE
                hiddenImageButton.visibility = View.VISIBLE
                hiddenImageZone.visibility = View.VISIBLE
            }

            hiddenImageButton.setOnClickListener {
                sceneSelectorLayout.visibility = View.INVISIBLE
                hiddenImageButton.visibility = View.INVISIBLE
                hiddenImageZone.visibility = View.INVISIBLE
            }

            hiddenImageZone.setOnClickListener {
                sceneSelectorLayout.visibility = View.INVISIBLE
                hiddenImageButton.visibility = View.INVISIBLE
                hiddenImageZone.visibility = View.INVISIBLE
            }

            newsButton.setOnClickListener{
                val intent = Intent(this, InitActivity::class.java)
                startActivity(intent)
            }

            profileButton.setOnClickListener{
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
            }

        //LogOut
        val outButton: Button = findViewById(R.id.logOutButton)
        outButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener{
            db.collection("Users").document(email).set(
                hashMapOf("provider" to provider,
                "username" to usernameText.text.toString(),
                "phone" to phoneText.text.toString())
            )
        }

        getButton.setOnClickListener{
            db.collection("Users").document(email).get().addOnSuccessListener {
                usernameText.setText(it.get("username") as String?)
                phoneText.setText(it.get("phone") as String?)
            }
        }

        deleteButton.setOnClickListener{
            db.collection("Users").document(email).delete().addOnSuccessListener {
                usernameText.setText(" ")
                phoneText.setText(" ")
            }
        }

    }
}