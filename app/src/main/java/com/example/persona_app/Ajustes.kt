package com.example.persona_app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView

class Ajustes : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val backgroundImage: ImageFilterView = findViewById(R.id.backgroundmain)
        val button: Button = findViewById(R.id.change_color)

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false) // Cargar estado guardado


        updateTheme(backgroundImage)

        button.setOnClickListener {
            isDarkMode = !isDarkMode
            sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply() // Guardar estado
            updateTheme(backgroundImage)
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