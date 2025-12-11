package com.example.persona_app

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment

class Ajustes_fragment : Fragment(R.layout.fragment_ajustes_fragment) {

    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkMode: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundImage: ImageFilterView = view.findViewById(R.id.ColorCambia_2)
        val button: Button = view.findViewById(R.id.change_color)

        // SharedPreferences en fragment
        sharedPreferences = requireContext().getSharedPreferences("AppSettings", AppCompatActivity.MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false) // Cargar estado guardado

        updateTheme(backgroundImage)

        button.setOnClickListener {
            isDarkMode = !isDarkMode
            sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply() // Guardar estado
            updateTheme(backgroundImage)
        }
    }

    private fun updateTheme(backgroundImage: ImageFilterView) {
        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, requireContext().theme))
        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, requireContext().theme))
        }
    }
}
