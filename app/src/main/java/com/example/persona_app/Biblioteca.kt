package com.example.persona_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Biblioteca : AppCompatActivity() {

    private val steamApiKey = "DFDD5A1D4ABF350102931F27ECBA2F40" // Tu Steam API Key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biblioteca)

        val checkGamesButton: Button = findViewById(R.id.checkGamesButton)  // Cambio el nombre del botón
        val gameNameInput: EditText = findViewById(R.id.gameNameInput)

        // Configurar Firebase Firestore y obtener el usuario actual
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        // Verificar si el usuario está autenticado
        if (user != null) {
            val email = user.email.toString()

            // Obtener SteamID desde Firestore
            db.collection("Users").document(email).get().addOnSuccessListener { document ->
                val steamId = document.getString("SteamID")

                if (steamId != null) {
                    // Configurar el botón para buscar juegos
                    checkGamesButton.setOnClickListener {
                        getGamesForUser(steamId)
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
    }

    /**
     * Función para obtener los juegos del usuario.
     * @param steamId El SteamID del usuario.
     */
    private fun getGamesForUser(steamId: String) {
        val client = OkHttpClient()

        // URL para obtener los juegos del usuario
        val url = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=$steamApiKey&steamid=$steamId&format=json"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SteamAPI", "Error al realizar la solicitud: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@Biblioteca, "Error al obtener juegos. Detalles: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    // Verificar si la respuesta es exitosa
                    if (!response.isSuccessful) {
                        Log.e("SteamAPI", "Respuesta fallida: ${response.message}")
                        runOnUiThread {
                            Toast.makeText(this@Biblioteca, "Error al obtener juegos. Respuesta fallida.", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val jsonResponse = response.body?.string()
                    if (jsonResponse != null) {
                        try {
                            val jsonObject = JSONObject(jsonResponse)

                            // Verificar si contiene la lista de juegos
                            if (jsonObject.has("response")) {
                                val responseObj = jsonObject.getJSONObject("response")

                                // Obtener la lista de juegos
                                if (responseObj.has("games")) {
                                    val games = responseObj.getJSONArray("games")

                                    // Crear una lista de nombres de juegos
                                    val gameNames = mutableListOf<String>()
                                    for (i in 0 until games.length()) {
                                        val game = games.getJSONObject(i)
                                        val gameName = game.getString("name")
                                        gameNames.add(gameName)
                                    }

                                    // Mostrar los juegos en un Toast o en otro componente como un ListView
                                    runOnUiThread {
                                        if (gameNames.isNotEmpty()) {
                                            Toast.makeText(this@Biblioteca, "Juegos encontrados: ${gameNames.joinToString(", ")}", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@Biblioteca, "No se encontraron juegos para este usuario.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    runOnUiThread {
                                        Toast.makeText(this@Biblioteca, "No se encontraron juegos para este usuario.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Log.e("SteamAPI", "Respuesta no contiene 'response'.")
                                runOnUiThread {
                                    Toast.makeText(this@Biblioteca, "Error al obtener los juegos: Respuesta incompleta.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SteamAPI", "Error al procesar JSON: ${e.message}")
                            runOnUiThread {
                                Toast.makeText(this@Biblioteca, "Error al procesar los datos.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.e("SteamAPI", "La respuesta del servidor es nula.")
                        runOnUiThread {
                            Toast.makeText(this@Biblioteca, "Error al obtener juegos: Respuesta vacía.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}