package com.example.persona_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class Profile : AppCompatActivity() {

    private val steamApiKey = "DFDD5A1D4ABF350102931F27ECBA2F40"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Selector
            val showImageButton: Button = findViewById(R.id.menuOpen)
            val hiddenImageButton: Button = findViewById(R.id.menuClose)
            val hiddenImageZone: Button = findViewById(R.id.CloseZone)
            val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)

            val newsButton: Button = findViewById(R.id.news_button)
            val profileButton: Button = findViewById(R.id.Profile_Button)
            val ajustesButton: Button = findViewById(R.id.ajustes_Buton)
            val bibliotecaButton: Button = findViewById(R.id.BibliotecaButton)

            val saveButton: Button = findViewById(R.id.SaveButton)
            val getButton: Button = findViewById(R.id.GetButton)
            val deleteButton: Button = findViewById(R.id.DeleteButton)

            val usernameText: EditText = findViewById(R.id.usernameText)
            val phoneText: EditText = findViewById(R.id.PhoneText)

            val avatarImageView: ImageView = findViewById(R.id.ImageProfile)

        //Base de datos
            val db = FirebaseFirestore.getInstance()

            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email.toString()
            val provider = user?.providerId

        if (user != null) {
            db.collection("Users").document(email).get().addOnSuccessListener { document ->
                // Obtener datos desde Firestore
                val username = document.getString("SteamID")
                val phone = document.getString("phone")
                val steamId = username // Usaremos el "username" como Steam ID o Vanity URL

                // Mostrar datos en los campos de texto
                usernameText.setText(username)
                phoneText.setText(phone)

                // Cargar avatar y nombre desde Steam
                if (!steamId.isNullOrEmpty()) {
                    resolveVanityURL(steamId) { resolvedSteamId ->
                        if (resolvedSteamId != null) {
                            fetchSteamProfile(resolvedSteamId) { avatarUrl, personaName ->
                                runOnUiThread {
                                    if (avatarUrl != null) {
                                        // Mostrar avatar en el ImageView
                                        Glide.with(this).load(avatarUrl).into(avatarImageView)
                                    }
                                }
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos del usuario.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }

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

        //LogOut
        val outButton: Button = findViewById(R.id.logOutButton)
        outButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener{

            Log.d("email: ", email + " SteamID: " + usernameText.text.toString() + " Phonre: " + phoneText.text.toString())

            //STEAM
            val input = usernameText.text.toString()

            if (input.isNotEmpty()) {
                //convertir Vanity URL a SteamID64
                resolveVanityURL(input) { steamId ->
                    if (steamId != null) {
                        fetchSteamProfile(steamId) { avatarUrl, personaName ->
                            runOnUiThread {
                                if (avatarUrl != null) {
                                    // Mostrar avatar y guardamos nombre y telefono
                                    db.collection("Users").document(email).set(
                                        hashMapOf("provider" to provider,
                                            "SteamID" to usernameText.text.toString(),
                                            "phone" to phoneText.text.toString())
                                    )
                                    Glide.with(this).load(avatarUrl).into(avatarImageView)
                                    Toast.makeText(this, "Bienvenido, $personaName!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "No se encontró el perfil.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "ID no válido o perfil no encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, introduce un Steam ID o Vanity URL.", Toast.LENGTH_SHORT).show()
            }
        }

        getButton.setOnClickListener{
            db.collection("Users").document(email).get().addOnSuccessListener {
                usernameText.setText(it.get("SteamID") as String?)
                phoneText.setText(it.get("phone") as String?)
            }
        }

        deleteButton.setOnClickListener{
            db.collection("Users").document(email).delete().addOnSuccessListener {
                usernameText.setText("")
                phoneText.setText("")
                usernameText.setText("")
            }
        }
    }

    //CHAT GPT
    private fun resolveVanityURL(vanityUrl: String, callback: (String?) -> Unit) {
        val client = OkHttpClient()
        val url = "https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/" +
                "?key=$steamApiKey&vanityurl=$vanityUrl"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SteamAPI", "Error al resolver Vanity URL: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("SteamAPI", "Respuesta fallida: ${response.message}")
                        callback(null)
                        return
                    }

                    val jsonResponse = response.body?.string()
                    if (jsonResponse != null) {
                        try {
                            val jsonObject = JSONObject(jsonResponse)
                            val responseObj = jsonObject.getJSONObject("response")
                            val steamId = responseObj.optString("steamid", null)
                            callback(steamId)
                        } catch (e: Exception) {
                            Log.e("SteamAPI", "Error al procesar JSON: ${e.message}")
                            callback(null)
                        }
                    }
                }
            }
        })
    }

    private fun fetchSteamProfile(steamId: String, callback: (String?, String?) -> Unit) {
        val client = OkHttpClient()
        val url = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/" +
                "?key=$steamApiKey&steamids=$steamId"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SteamAPI", "Error al obtener perfil: ${e.message}")
                callback(null, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("SteamAPI", "Respuesta fallida: ${response.message}")
                        callback(null, null)
                        return
                    }

                    val jsonResponse = response.body?.string()
                    if (jsonResponse != null) {
                        try {
                            val jsonObject = JSONObject(jsonResponse)
                            val players = jsonObject.getJSONObject("response").getJSONArray("players")
                            if (players.length() > 0) {
                                val player = players.getJSONObject(0)
                                val avatarUrl = player.getString("avatarfull")
                                val personaName = player.getString("personaname")
                                callback(avatarUrl, personaName)
                            } else {
                                callback(null, null)
                            }
                        } catch (e: Exception) {
                            Log.e("SteamAPI", "Error al procesar JSON: ${e.message}")
                            callback(null, null)
                        }
                    }
                }
            }
        })
    }
}