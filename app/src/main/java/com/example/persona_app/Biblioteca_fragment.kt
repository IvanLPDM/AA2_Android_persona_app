package com.example.persona_app

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Biblioteca_fragment : Fragment(R.layout.fragment_biblioteca_fragment) {

    private val steamApiKey = "DFDD5A1D4ABF350102931F27ECBA2F40"
    private lateinit var gameAdapter: GameAdapter
    private val gameList = mutableListOf<Game>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundImage: ImageFilterView = view.findViewById(R.id.CambiaColor)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewGames)
        val gameNameInput: EditText = view.findViewById(R.id.inputBuscarID)

        sharedPreferences = requireContext().getSharedPreferences("AppSettings", androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Aplicar tema
        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, requireContext().theme))
            //selectorImage.setImageResource(R.mipmap.selector_library_v2)
        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, requireContext().theme))
            //selectorImage.setImageResource(R.mipmap.selector_library)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        gameAdapter = GameAdapter(requireContext(), gameList) { gameName ->
            Toast.makeText(requireContext(), "Juego seleccionado: $gameName", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = gameAdapter

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val email = user.email.toString()
            db.collection("Users").document(email).get().addOnSuccessListener { document ->
                val storedSteamId = document.getString("SteamID")
                if (storedSteamId != null) {
                    if (storedSteamId.matches(Regex("\\d{17}"))) {
                        getGamesForUser(storedSteamId)
                    } else {
                        resolveVanityURL(storedSteamId) { resolvedSteamId ->
                            if (resolvedSteamId != null) {
                                getGamesForUser(resolvedSteamId)
                            } else {
                                Toast.makeText(requireContext(), "No se pudo resolver el SteamID.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No se encontró tu SteamID en la base de datos.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error al obtener SteamID.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }

        gameNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase().trim()
                filterGames(query)
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
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error al resolver el SteamID.", Toast.LENGTH_SHORT).show()
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
        if (query.isEmpty()) {
            gameAdapter.updateGames(gameList)
        } else {
            val filteredList = gameList.filter { it.name.lowercase().contains(query) }
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
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error al obtener juegos.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("SteamAPI", "Respuesta fallida: ${response.message}")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Error al obtener juegos.", Toast.LENGTH_SHORT).show()
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
                                val newGameList = mutableListOf<Game>()

                                for (i in 0 until games.length()) {
                                    val game = games.getJSONObject(i)
                                    val gameName = game.optString("name", "Desconocido")
                                    val gameId = game.optString("gameName", "")
                                    val appId = game.optInt("appid", 0)
                                    val imageUrl = "https://cdn.cloudflare.steamstatic.com/steam/apps/$appId/header.jpg"

                                    newGameList.add(Game(gameId, gameName, imageUrl))
                                }

                                requireActivity().runOnUiThread {
                                    if (newGameList.isNotEmpty()) {
                                        gameList.clear()
                                        gameList.addAll(newGameList)
                                        gameAdapter.updateGames(gameList)
                                    } else {
                                        Toast.makeText(requireContext(), "No se encontraron juegos.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(requireContext(), "No se encontraron juegos.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SteamAPI", "Error al procesar JSON: ${e.message}")
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "Error al procesar los datos.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }
}
