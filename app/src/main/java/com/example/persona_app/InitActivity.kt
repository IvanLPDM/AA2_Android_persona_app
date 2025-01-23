package com.example.persona_app

import NewsAdapter
import NewsItem
import NewsResponse
import SteamApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)



        val showImageButton: Button = findViewById(R.id.menuOpen)
        val hiddenImageButton: Button = findViewById(R.id.menuClose)
        val hiddenImageZone: Button = findViewById(R.id.CloseZone)
        val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)

        val newsButton: Button = findViewById(R.id.news_button)
        val profileButton: Button = findViewById(R.id.Profile_Button)
        val ajustesButton: Button = findViewById(R.id.ajustes_Buton)

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

        val bundle = intent.extras

    }
}