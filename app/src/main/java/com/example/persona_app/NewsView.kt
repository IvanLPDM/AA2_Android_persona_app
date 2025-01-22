import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.persona_app.R

class NewsView(
    private val context: Context,
    private val newsList: List<NewsItem>
) : RecyclerView.Adapter<NewsView.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsImage: ImageView = view.findViewById(R.id.newsImage)
        val newsTitle: TextView = view.findViewById(R.id.newsTitle)
        val openNewsButton: Button = view.findViewById(R.id.openNewsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.newsTitle.text = newsItem.title

        // Cargar la imagen con Glide
        Glide.with(context)
            .load(newsItem.imageUrl).into(holder.newsImage)
            //.placeholder(R.drawable.placeholder_image)
            //.error(R.drawable.error_image)


        // Configurar el botón para abrir la URL
        holder.openNewsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = newsList.size
}