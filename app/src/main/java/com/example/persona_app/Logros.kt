package com.example.persona_app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Logros : AppCompatActivity() {

    private val steamApiKey = "DFDD5A1D4ABF350102931F27ECBA2F40"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Logros_Adapter
    private val logrosList = mutableListOf<Logros_item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logros)

        recyclerView = findViewById(R.id.recyclerViewLogros)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Logros_Adapter(logrosList)
        recyclerView.adapter = adapter

        //recibimos el Key_ID del juego
        val appId = intent.getIntExtra("APP_ID", -1)
        val inputSteamId = intent.getStringExtra("STEAM_ID")

        Toast.makeText(
            this,
            "APP_ID: $appId\nSTEAM_INPUT: $inputSteamId",
            Toast.LENGTH_LONG
        ).show()

        if (appId == -1 || inputSteamId.isNullOrEmpty()) {
            Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

            //  Si es Vanity → resolver primero
            resolveVanityURL(inputSteamId) { resolvedSteamId ->
                runOnUiThread {
                    if (resolvedSteamId == null) {
                        Toast.makeText(
                            this,
                            "No se pudo resolver Vanity URL",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "SteamID64: $resolvedSteamId",
                            Toast.LENGTH_SHORT
                        ).show()
                        //Recogemos los logros, pero necesitamos la funcion de vanity para tener la direccion del jugador
                        getAchievements(appId, resolvedSteamId)
                    }
                }
            }
        }

    private fun resolveVanityURL(vanityUrl: String, callback: (String?) -> Unit) {
        val client = OkHttpClient()

        val url =
            "https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/" +
                    "?key=$steamApiKey&vanityurl=$vanityUrl"

        Log.d("SteamAPI", "Resolve URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("SteamAPI", "Vanity error: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("SteamAPI", "Vanity HTTP ${response.code}")
                        callback(null)
                        return
                    }

                    val json = response.body?.string() ?: run {
                        callback(null)
                        return
                    }

                    try {
                        val root = JSONObject(json)
                        val steamId =
                            root.getJSONObject("response").optString("steamid", null)

                        callback(steamId)
                    } catch (e: Exception) {
                        Log.e("SteamAPI", "Vanity parse error")
                        callback(null)
                    }
                }
            }
        })
    }

    // OBTENER LOGROS
    private fun getAchievements(appId: Int, steamId: String) {
        val client = OkHttpClient()

        val url =
            "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1/" +
                    "?key=$steamApiKey&steamid=$steamId&appid=$appId"

        Log.d("SteamAPI", "Achievements URL: $url")

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@Logros,
                        "Error de red",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                this@Logros,
                                "Error ${response.code}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        return
                    }

                    val json = response.body?.string() ?: return
                    parseAchievements(json)
                }
            }
        })
    }

    // PARSEAR LOGROS

    private fun parseAchievements(json: String) {
        try {
            val root = JSONObject(json)

            if (!root.has("playerstats")) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Este juego no tiene logros",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

            val achievements =
                root.getJSONObject("playerstats").getJSONArray("achievements")

            val nuevosLogros = mutableListOf<Logros_item>()

            for (i in 0 until achievements.length()) {
                val ach = achievements.getJSONObject(i)
                val name = ach.getString("apiname")
                val achieved = ach.getInt("achieved") == 1

                nuevosLogros.add(Logros_item(name, achieved))
            }

            runOnUiThread {
                logrosList.clear()
                logrosList.addAll(nuevosLogros)
                adapter.update(logrosList)

                Toast.makeText(
                    this,
                    "Logros cargados: ${logrosList.size}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {
            Log.e("SteamAPI", "Parse error: ${e.message}")
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Error al procesar logros",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}