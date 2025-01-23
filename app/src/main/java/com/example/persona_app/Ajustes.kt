package com.example.persona_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout

class Ajustes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val showImageButton: Button = findViewById(R.id.menuOpen2)
        val hiddenImageButton: Button = findViewById(R.id.menuClose2)
        val hiddenImageZone: Button = findViewById(R.id.closeZone)
        val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)

        val newsButton: Button = findViewById(R.id.news_button)
        val profileButton: Button = findViewById(R.id.Profile_Button)
        val ajustesButton: Button = findViewById(R.id.ajustes_Buton)

        //UI
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

        ajustesButton.setOnClickListener{
            val intent = Intent(this, Ajustes::class.java)
            startActivity(intent)
        }

        

    }
}