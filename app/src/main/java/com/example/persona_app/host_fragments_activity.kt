package com.example.persona_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class host_fragments_activity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_fragments_activity)

        // Inicializar views despues de setContentView
        bottomNavigationView = findViewById(R.id.navbar)

        // Uso loadFragmentSafe para evitar crash
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.init -> {
                    loadFragmentSafe(init())
                    true
                }
                R.id.perfil -> {
                    loadFragmentSafe(Profile_fragment())
                    true
                }
                R.id.ajustes -> {
                    loadFragmentSafe(Ajustes_fragment())
                    true
                }
                R.id.biblioteca -> {
                    loadFragmentSafe(Biblioteca_fragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState != null) {
            val fragment = supportFragmentManager.getFragment(savedInstanceState, "CURRENT_FRAGMENT")
            if (fragment != null) {
                loadFragmentSafe(fragment)
            }
        } else {
            loadFragmentSafe(init())
        }
    }

    private fun loadFragmentSafe(fragment: Fragment) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction().replace(R.id.frame, fragment)
        if (!fm.isStateSaved) {
            ft.commit()
        } else {
            ft.commitAllowingStateLoss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame)
        if (currentFragment != null) {
            supportFragmentManager.putFragment(outState, "CURRENT_FRAGMENT", currentFragment)
        }
    }
}
