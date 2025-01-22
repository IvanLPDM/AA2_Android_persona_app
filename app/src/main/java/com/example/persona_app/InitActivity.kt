package com.example.persona_app

import NewsResponse
import SteamApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope



class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        val steamApi = SteamApiService.retrofit.create(SteamApi::class.java)


        val showImageButton: Button = findViewById(R.id.menuOpen)
        val hiddenImageButton: Button = findViewById(R.id.menuClose)
        val hiddenImageZone: Button = findViewById(R.id.CloseZone)
        val sceneSelectorLayout: ConstraintLayout = findViewById(R.id.scene_selector_layout)

        val newsButton: Button = findViewById(R.id.news_button)
        val profileButton: Button = findViewById(R.id.Profile_Button)

        //API too
        val newsImageView: ImageView = findViewById(R.id.newsImageView)
        val newTextView: TextView = findViewById(R.id.TitleNew)
        val buttonNew: Button = findViewById(R.id.New)
        var Url : String = ""

        //API NEWS
        try {//entramos en la api
            steamApi.getNewsForApp("1687950", "DFDD5A1D4ABF350102931F27ECBA2F40").enqueue(object :
                Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val newsItems = response.body()?.appnews?.newsitems
                        if (newsItems != null) {
                            newsItems.forEach(){
                            Log.d("SteamAPI_News", "Title: ${it.title}, " + "URL: ${it.url}, " + "URL Image: ${it.imageUrl}")

                                newTextView.setText(it.title)
                                Url = it.url                            }


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

            buttonNew.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                if (Url.isNotEmpty()) {
                    intent.data = Uri.parse(Url)
                } else {
                    intent.data = Uri.parse("https://default-url.com")
                }

                startActivity(intent)
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

        val bundle = intent.extras
        val email = bundle?.getString("email")

        //Setup
        setup(email?: "")

        //Guardar datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()
    }





    private fun setup(email:String)
    {
        val emailText: TextView = findViewById(R.id.emailTextView)

        emailText.text = email


    }
}