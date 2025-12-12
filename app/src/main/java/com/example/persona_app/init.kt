package com.example.persona_app

import NewsAdapter
import NewsResponse
import SteamApi
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class init : Fragment(R.layout.fragment_init) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundImage: ImageFilterView = view.findViewById(R.id.Color_Cambia_3)

        val newsRecyclerView: RecyclerView = view.findViewById(R.id.newsRecyclerView)

        val sharedPreferences =
            requireContext().getSharedPreferences("AppSettings", androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, requireContext().theme))
        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, requireContext().theme))
        }

        // FIREBASE
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val email = user.email.toString()
            db.collection("Users").document(email).get().addOnSuccessListener { document ->
                if (!isAdded) return@addOnSuccessListener
                if (document.exists()) {
                    val steamId = document.getString("SteamID")
                    if (!steamId.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Bienvenido, $steamId!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Inicia sesión en Steam desde Profile.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // API NEWS
        val steamApi = SteamApiService.retrofit.create(SteamApi::class.java)
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        try {
            steamApi.getNewsForApp("1687950", "DFDD5A1D4ABF350102931F27ECBA2F40")
                .enqueue(object : Callback<NewsResponse> {
                    override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                        if (!isAdded) return
                        if (response.isSuccessful) {
                            val newsItems = response.body()?.appnews?.newsitems
                            if (newsItems != null) {
                                val adapter = NewsAdapter(requireContext(), newsItems) { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    startActivity(intent)
                                }
                                newsRecyclerView.adapter = adapter
                            }
                        } else {
                            Log.e("SteamAPI_Err", "Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                        if (!isAdded) return
                        Log.e("SteamAPI_Err", "Failed: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            Log.e("SteamAPI_Err", "Unexpected error: ${e.localizedMessage}")
        }
    }
}