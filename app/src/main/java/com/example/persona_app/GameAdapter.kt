package com.example.persona_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class Game(
    val id: String,
    val name: String,
    val imageUrl: String
)

class GameAdapter(
    private val context: Context,
    private var gameList: List<Game>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {


    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImageView: ImageView = itemView.findViewById(R.id.gameImage)
        val gameTitleTextView: TextView = itemView.findViewById(R.id.gameTitle)
    }

    // Infla el diseño del item para el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    // Vincula los datos con las vistas de cada item
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList[position]

        holder.gameTitleTextView.text = game.name

        Glide.with(context)
            .load(game.imageUrl)
            .into(holder.gameImageView)

        holder.itemView.setOnClickListener {
            onItemClicked(game.name) // Puedes pasar el ID del juego o nombre
        }
    }

    override fun getItemCount(): Int = gameList.size

    // Actualizar los juegos cuando se obtienen nuevos
    fun updateGames(newGameList: List<Game>) {
        gameList = newGameList
        notifyDataSetChanged()
    }
}