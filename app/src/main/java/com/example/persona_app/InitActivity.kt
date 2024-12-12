package com.example.persona_app

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.os.Bundle
import android.util.Log

class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        val showImageButton: Button = findViewById(R.id.menuOpen)
        val hiddenImageButton: Button = findViewById(R.id.menuClose)
        val hiddenImageZone: Button = findViewById(R.id.CloseZone)
        val menuImage: ImageView = findViewById(R.id.Selector)

        showImageButton.setOnClickListener {
            menuImage.visibility = View.VISIBLE
            hiddenImageButton.visibility = View.VISIBLE
        }

        hiddenImageButton.setOnClickListener {
            menuImage.visibility = View.INVISIBLE
            hiddenImageButton.visibility = View.INVISIBLE
        }

        hiddenImageZone.setOnClickListener {
            menuImage.visibility = View.INVISIBLE
            hiddenImageButton.visibility = View.INVISIBLE
        }
    }
}