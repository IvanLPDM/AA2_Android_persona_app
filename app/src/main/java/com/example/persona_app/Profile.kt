package com.example.persona_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

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

    }
}