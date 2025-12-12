package com.example.persona_app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Profile_fragment : Fragment(R.layout.fragment_profile_fragment) {

    private val steamApiKey = "DFDD5A1D4ABF350102931F27ECBA2F40"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundImage: ImageFilterView = view.findViewById(R.id.Color_Cambia)

        sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Aplicar tema
        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, requireContext().theme))
            //selectorImage.setImageResource(R.mipmap.selector_profile_v2)
        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, requireContext().theme))
            //selectorImage.setImageResource(R.mipmap.screen_profile)
        }

        val saveButton: Button = view.findViewById(R.id.SaveButton)
        val getButton: Button = view.findViewById(R.id.GetButton)
        val deleteButton: Button = view.findViewById(R.id.DeleteButton)

        val usernameText: EditText = view.findViewById(R.id.usernameText)
        val phoneText: EditText = view.findViewById(R.id.PhoneText)

        val avatarImageView: ImageView = view.findViewById(R.id.ImageProfile)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        // Base de datos
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email.toString()
        val provider = user?.providerId

        if (user != null) {
            progressBar.visibility = View.VISIBLE
            db.collection("Users").document(email).get().addOnSuccessListener { document ->
                val username = document.getString("SteamID")
                val phone = document.getString("phone")
                val steamId = username

                usernameText.setText(username)
                phoneText.setText(phone)

                if (!steamId.isNullOrEmpty()) {
                    resolveVanityURL(steamId) { resolvedSteamId ->
                        if (resolvedSteamId != null) {
                            fetchSteamProfile(resolvedSteamId) { avatarUrl, personaName ->
                                requireActivity().runOnUiThread {
                                    if (avatarUrl != null) {
                                        progressBar.visibility = View.GONE
                                        Glide.with(requireContext()).load(avatarUrl).into(avatarImageView)
                                    }
                                }
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error al cargar datos del usuario.", Toast.LENGTH_SHORT).show()
            }
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }

        //Salir de la app
        val exitButton: Button = view.findViewById(R.id.Salir)
        exitButton.setOnClickListener {
            requireActivity().finishAffinity() // Cierra todas las Activities abiertas
            System.exit(0) // Termina el proceso de la app
        }



        // LogOut
        val outButton: Button = view.findViewById(R.id.logOutButton)
        outButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sharedPreferences.edit().clear().apply()
            // Redirigir a MainActivity
        }

        saveButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val input = usernameText.text.toString()
            if (input.isNotEmpty()) {
                resolveVanityURL(input) { steamId ->
                    if (steamId != null) {
                        fetchSteamProfile(steamId) { avatarUrl, personaName ->
                            requireActivity().runOnUiThread {
                                progressBar.visibility = View.GONE
                                if (avatarUrl != null) {
                                    db.collection("Users").document(email).set(
                                        hashMapOf(
                                            "provider" to provider,
                                            "SteamID" to usernameText.text.toString(),
                                            "phone" to phoneText.text.toString()
                                        )
                                    )
                                    Glide.with(requireContext()).load(avatarUrl).into(avatarImageView)
                                    Toast.makeText(requireContext(), "Bienvenido, $personaName!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "No se encontró el perfil.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "ID no válido o perfil no encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Por favor, introduce un Steam ID o Vanity URL.", Toast.LENGTH_SHORT).show()
            }
        }

        getButton.setOnClickListener {
            db.collection("Users").document(email).get().addOnSuccessListener {
                usernameText.setText(it.get("SteamID") as String?)
                phoneText.setText(it.get("phone") as String?)
            }
        }

        deleteButton.setOnClickListener {
            db.collection("Users").document(email).delete().addOnSuccessListener {
                usernameText.setText("")
                phoneText.setText("")
            }
        }
    }

    private fun resolveVanityURL(vanityUrl: String, callback: (String?) -> Unit) {
        val client = OkHttpClient()
            val url = "https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/?key=$steamApiKey&vanityurl=$vanityUrl"
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
        val url = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=$steamApiKey&steamids=$steamId"
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
