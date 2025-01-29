package com.example.persona_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Biblioteca : AppCompatActivity() {

    private val steamApiKey = "DFDD5A1D4ABF350102931F27ECBA2F40"
    private lateinit var gameAdapter: GameAdapter
    private val gameList = mutableListOf<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biblioteca)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewGames)
        recyclerView.layoutManager = LinearLayoutManager(this)
        gameAdapter = GameAdapter(this, gameList) { gameName ->
            // Puedes manejar el click para abrir detalles del juego
            Toast.makeText(this, "Juego seleccionado: $gameName", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = gameAdapter

        val gameNameInput: EditText = findViewById(R.id.inputBuscarID)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val email = user.email.toString()

            db.collection("Users").document(email).get().addOnSuccessListener { document ->
                val storedSteamId = document.getString("SteamID")

                if (storedSteamId != null) {
                    // Verificar si el SteamID es un Vanity URL
                    if (storedSteamId.matches(Regex("\\d{17}"))) {
                        // Ya es un SteamID64, usarlo directamente

                        getGamesForUser(storedSteamId)

                    } else {
                        // Es un Vanity URL, hay que resolverlo
                        resolveVanityURL(storedSteamId) { resolvedSteamId ->
                            if (resolvedSteamId != null) {
                                getGamesForUser(resolvedSteamId)

                            } else {
                                Toast.makeText(this, "No se pudo resolver el SteamID.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "No se encontró tu SteamID en la base de datos.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener SteamID.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }

        // Agregar el TextWatcher al EditText
        gameNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase().trim() // Obtener el texto de búsqueda en minúsculas
                filterGames(query) // Filtrar los juegos
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun resolveVanityURL(vanityUrl: String, callback: (String?) -> Unit) {
        val client = OkHttpClient()
        val url = "https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/?key=$steamApiKey&vanityurl=$vanityUrl"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SteamAPI", "Error al resolver Vanity URL: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@Biblioteca, "Error al resolver el SteamID.", Toast.LENGTH_SHORT).show()
                }
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

    private fun filterGames(query: String) {
        Log.d("Biblioteca", "Filtrando con el texto: $query")
        if (query.isEmpty()) {
            gameAdapter.updateGames(gameList)
        } else {
            val filteredList = gameList.filter { it.name.lowercase().contains(query) }
            Log.d("Biblioteca", "Juegos filtrados: ${filteredList.size}")
            gameAdapter.updateGames(filteredList)
        }
    }

    private fun getGamesForUser(steamId: String) {
        val client = OkHttpClient()
        val url = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=$steamApiKey&steamid=$steamId&format=json&include_appinfo=true"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SteamAPI", "Error al obtener juegos: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@Biblioteca, "Error al obtener juegos.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("SteamAPI", "Respuesta fallida: ${response.message}")
                        runOnUiThread {
                            Toast.makeText(this@Biblioteca, "Error al obtener juegos.", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val jsonResponse = response.body?.string()
                    if (jsonResponse != null) {
                        try {
                            val jsonObject = JSONObject(jsonResponse)
                            val responseObj = jsonObject.getJSONObject("response")

                            if (responseObj.has("games")) {
                                val games = responseObj.getJSONArray("games")
                                val gameList = mutableListOf<Game>()

                                for (i in 0 until games.length()) {
                                    val game = games.getJSONObject(i)
                                    val gameName = game.optString("name", "Desconocido")
                                    val gameImageUrl = game.optString("img_logo_url", "")
                                    val gameId = game.optString("gameName", "")
                                    val appId = game.optInt("appid", 0)
                                    val imageUrl = "https://cdn.cloudflare.steamstatic.com/steam/apps/$appId/header.jpg"

                                    // Crear un objeto Game con los datos obtenidos
                                    gameList.add(Game(gameId, gameName, imageUrl))
                                }

                                // Actualizar el RecyclerView en el hilo principal
                                runOnUiThread {
                                    if (gameList.isNotEmpty()) {
                                        this@Biblioteca.gameList.clear() // Limpiar la lista anterior
                                        this@Biblioteca.gameList.addAll(gameList) // Agregar los nuevos juegos
                                        gameAdapter.updateGames(this@Biblioteca.gameList)
                                    } else {
                                        Toast.makeText(this@Biblioteca, "No se encontraron juegos.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this@Biblioteca, "No se encontraron juegos.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SteamAPI", "Error al procesar JSON: ${e.message}")
                            runOnUiThread {
                                Toast.makeText(this@Biblioteca, "Error al procesar los datos.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }
}