package com.example.persona_app

import NewsAdapter
import NewsResponse
import SteamApi
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        val backgroundImage: ImageFilterView = findViewById(R.id.Color_Cambia_3)

        val showImageButton: Button = findViewById(R.id.menuOpen)
        val hiddenImageButton: Button = findViewById(R.id.menuClose)
        val hiddenImageZone: Button = findViewById(R.id.CloseZone)
        val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)
        val selectorImage: ImageView = findViewById(R.id.Selector)

        val newsButton: Button = findViewById(R.id.news_button)
        val profileButton: Button = findViewById(R.id.Profile_Button)
        val ajustesButton: Button = findViewById(R.id.ajustes_Buton)
        val bibliotecaButton: Button = findViewById(R.id.BibliotecaButton)

        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Aplicar el tema correcto
        if (isDarkMode) {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_2, theme))
            selectorImage.setImageResource(R.mipmap.selector_new_v2r)

        } else {
            backgroundImage.setColorFilter(resources.getColor(R.color.style_1, theme))
            selectorImage.setImageResource(R.mipmap.screen_news)
        }



        //Base de datos para identificar si has iniciado session en Steam
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val email = user.email.toString()
            db.collection("Users").document(email).get().addOnSuccessListener { document ->
                if (document.exists()) {

                    val steamId = document.getString("SteamID")
                    if (!steamId.isNullOrEmpty()) {

                        Toast.makeText(this, "Bienvenido, $steamId!", Toast.LENGTH_SHORT)
                            .show()

                    } else
                        Toast.makeText(this, "Inicia session en Steam desde Profile.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        //API
        val steamApi = SteamApiService.retrofit.create(SteamApi::class.java)

        lateinit var newsRecyclerView: RecyclerView
        lateinit var newsAdapter: NewsAdapter

        newsRecyclerView = findViewById(R.id.newsRecyclerView)

        // Configuración del RecyclerView
        newsRecyclerView.layoutManager = LinearLayoutManager(this)


        //API NEWS
        try {//entramos en la api
            steamApi.getNewsForApp("1687950", "DFDD5A1D4ABF350102931F27ECBA2F40").enqueue(object :
                Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val newsItems = response.body()?.appnews?.newsitems
                        if (newsItems != null) {
                            //leemos todas las news que nos devuelve la api
                            newsAdapter = NewsAdapter(this@InitActivity, newsItems) { url ->

                                // Abrimos la URL cuando se hace clic
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }

                            newsRecyclerView.adapter = newsAdapter

                        }
                    } else {
                        Log.e("SteamAPI_Err", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Log.e("SteamAPI_Err", "Failed: ${t.message}")
                }
            })} catch (e: Exception) {
            Log.e("SteamAPI_Err", "Unexpected error: ${e.localizedMessage}")
        }




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

        bibliotecaButton.setOnClickListener{
            val intent = Intent(this, Biblioteca::class.java)
            startActivity(intent)
        }

        val bundle = intent.extras

    }
}