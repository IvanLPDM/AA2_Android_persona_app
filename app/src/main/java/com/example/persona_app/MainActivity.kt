package com.example.persona_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Para ver la variable de username
        //val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
    }
}