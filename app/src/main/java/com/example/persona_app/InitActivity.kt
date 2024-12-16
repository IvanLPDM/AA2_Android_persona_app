package com.example.persona_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        val showImageButton: Button = findViewById(R.id.menuOpen)
        val hiddenImageButton: Button = findViewById(R.id.menuClose)
        val hiddenImageZone: Button = findViewById(R.id.CloseZone)
        val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)

        val newsButton: Button = findViewById(R.id.news_button)

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

        val bundle = intent.extras
        val email = bundle?.getString("email")
        setup(email?: "")
    }

    private fun setup(email:String)
    {
        val emailText: TextView = findViewById(R.id.emailTextView)

        emailText.text = email
    }
}