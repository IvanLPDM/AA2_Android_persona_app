import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

object SteamApiService {
    // La URL base de la API de Steam
    private const val BASE_URL = "https://api.steampowered.com/"

    // Cliente HTTP para manejar solicitudes
    private val client = OkHttpClient.Builder().build()

    // Instancia de Retrofit
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // URL base
        .client(client)    // Cliente HTTP configurado
        .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON
        .build()

    // Crea una instancia de SteamApi
    val api: SteamApi = retrofit.create(SteamApi::class.java)
}



interface SteamApi {
    @GET("ISteamNews/GetNewsForApp/v2/")
    fun getNewsForApp(
        @Query("appid") appId: String, // ID del juego
        @Query("key") apiKey: String   // Tu clave API
    ): Call<NewsResponse>
}

data class NewsResponse(
    val appnews: AppNews
)

data class AppNews(
    val newsitems: List<NewsItem>
)

data class NewsItem(
    val title: String,
    val contents: String,
    val url: String,
    val imageUrl: String
)

