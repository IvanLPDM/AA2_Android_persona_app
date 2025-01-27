package com.example.persona_app

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Ajustes : AppCompatActivity() {

    private val steamApiKey = "DFDD5A1D4ABF350102931F27ECBA2F40"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val steamIdInput: EditText = findViewById(R.id.steam_id_input)
        val fetchProfileButton: Button = findViewById(R.id.fetch_profile_button)
        val avatarImageView: ImageView = findViewById(R.id.avatar_image_view)

        fetchProfileButton.setOnClickListener {
            val input = steamIdInput.text.toString()

            if (input.isNotEmpty()) {
                // Intentar convertir Vanity URL a SteamID64
                resolveVanityURL(input) { steamId ->
                    if (steamId != null) {
                        fetchSteamProfile(steamId) { avatarUrl, personaName ->
                            runOnUiThread {
                                if (avatarUrl != null) {
                                    // Mostrar avatar y nombre
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
    }

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