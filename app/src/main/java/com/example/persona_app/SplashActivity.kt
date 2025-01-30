package com.example.persona_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val backgroundImage: ImageFilterView = findViewById(R.id.ColorCambia)

        // Obtener el estado del tema guardado en SharedPreferences
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Aplicar el tema correcto
        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, theme))
        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, theme))
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza SplashActivity
        }, 3000)
    }
}