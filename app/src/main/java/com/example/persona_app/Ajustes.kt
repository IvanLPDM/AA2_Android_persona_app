package com.example.persona_app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        val steamButton: Button = findViewById(R.id.SteamButton)

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

        /*steamButton.setOnClickListener {
            val YOURRETURNURL = "com.example.persona_app.com://auth/handler"
            val YOUR_REALM_URL = "https://com.example.persona_app.firebaseapp.com"

            val steamOpenIdUrl = "https://steamcommunity.com/openid/login" +
                    "?openid.ns=http://specs.openid.net/auth/2.0" +
                    "&openid.mode=checkid_setup" +
                    "&openid.return_to=$YOURRETURNURL" +
                    "&openid.realm=$YOUR_REALM_URL" +
                    "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select" +
                    "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"

            Log.d("SteamOpenID", "Generated URL: $steamOpenIdUrl")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(steamOpenIdUrl))
            startActivity(intent)
        }*/

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri: Uri? = intent?.data

        if (uri != null) {
            Log.d("SteamOpenID", "Received URI: $uri")
            if (uri.toString().startsWith("com.example.persona_app://auth/handler")) {
                val parameters = uri.query
                Log.d("SteamOpenID", "Query parameters: $parameters")
                handleSteamResponse(parameters)
            } else {
                Log.e("SteamOpenID", "Unexpected URI: $uri")
            }
        } else {
            Log.e("SteamOpenID", "No URI received")
        }
    }

    private fun handleSteamResponse(parameters: String?) {
        if (parameters != null) {
            val steamIdRegex = Regex("openid.claimed_id=.*id/(\\d+)")
            val match = steamIdRegex.find(parameters)
            val steamId = match?.groupValues?.get(1)

            if (steamId != null) {
                Log.d("SteamID", "Steam ID obtenido: $steamId")
                fetchSteamUserData(steamId)
            } else {
                Log.e("SteamID", "No se pudo extraer el Steam ID")
            }
        } else {
            Log.e("SteamResponse", "No se recibieron parámetros")
        }

    }

    private fun fetchSteamUserData(steamId: String) {
        val apiKey = "TU_API_KEY" // Tu clave de API de Steam
        val url = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=$apiKey&steamids=$steamId"

        // Usa una biblioteca HTTP como Retrofit o OkHttp
        Log.d("SteamAPI", "Fetching data from: $url")
    }

}