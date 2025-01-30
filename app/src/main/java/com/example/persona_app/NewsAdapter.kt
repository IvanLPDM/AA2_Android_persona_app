import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.persona_app.R


class NewsAdapter(
    private val context: Context,
    private var newsList: List<NewsItem>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.newsTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.openNewsButton)
        val newsImageView: ImageView = itemView.findViewById(R.id.newsImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    // Vincula los datos con las vistas de cada item
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]

        // Establecer el título, contenido y cargar la imagen desde la URL
        holder.titleTextView.text = newsItem.title

        if(newsItem.imageUrl != null)
        {
            Glide.with(context)
                .load(newsItem.imageUrl)
                .into(holder.newsImageView)
        }


        holder.contentTextView.setOnClickListener {
            onItemClicked(newsItem.url)
        }
    }

    override fun getItemCount(): Int = newsList.size

    fun updateNews(newNewsList: List<NewsItem>) {
        newsList = newNewsList
        notifyDataSetChanged()
    }
}