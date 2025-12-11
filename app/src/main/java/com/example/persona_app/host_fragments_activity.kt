package com.example.persona_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class host_fragments_activity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_fragments_activity)

        bottomNavigationView = findViewById(R.id.navbar)

        bottomNavigationView.setOnItemSelectedListener { item ->
            handleNavigationItemSelected(item.itemId)
        }

        loadFragment(init())
    }

    //Añadir Fragments
    private fun handleNavigationItemSelected(itemId: Int): Boolean {
        return when (itemId)
        {
            R.id.init ->{
                loadFragment(init())
                true
            }
            R.id.perfil->{
                loadFragment(Profile_fragment())
                true
            }
            R.id.ajustes->{
                loadFragment(Ajustes_fragment())
                true
            }
            R.id.biblioteca->{
                loadFragment(Biblioteca_fragment())
                true
            }
            else -> false
        }
    }

    private fun loadFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.frame, fragment).commit()
    }
}