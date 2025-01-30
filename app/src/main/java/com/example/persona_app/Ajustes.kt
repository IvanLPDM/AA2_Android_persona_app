package com.example.persona_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.constraintlayout.widget.ConstraintLayout

class Ajustes : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val backgroundImage: ImageFilterView = findViewById(R.id.ColorCambia_2)
        val button: Button = findViewById(R.id.change_color)

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false) // Cargar estado guardado


        updateTheme(backgroundImage)

        button.setOnClickListener {
            isDarkMode = !isDarkMode
            sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply() // Guardar estado
            updateTheme(backgroundImage)
        }

        val showImageButton: Button = findViewById(R.id.menuOpen)
        val hiddenImageButton: Button = findViewById(R.id.menuClose)
        val hiddenImageZone: Button = findViewById(R.id.CloseZone)
        val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)

        val newsButton: Button = findViewById(R.id.news_button)
        val profileButton: Button = findViewById(R.id.Profile_Button)
        val ajustesButton: Button = findViewById(R.id.ajustes_Buton)
        val bibliotecaButton: Button = findViewById(R.id.BibliotecaButton)

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

        bibliotecaButton.setOnClickListener{
            val intent = Intent(this, Biblioteca::class.java)
            startActivity(intent)
        }
    }

    // Aplicar color inicial según el estado guardado
    private fun updateTheme(backgroundImage: ImageFilterView) {
        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, theme))
        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, theme))
        }
    }
}