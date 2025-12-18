package com.example.persona_app

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class Logros_Adapter(
    private var logros: List<Logros_item>
) : RecyclerView.Adapter<Logros_Adapter.LogroViewHolder>() {

    inner class LogroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.logroNombre)
        val estado: TextView = view.findViewById(R.id.logroEstado)
        val icono: ImageView = view.findViewById(R.id.image_logro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_logros_item, parent, false)
        return LogroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogroViewHolder, position: Int) {
        val logro = logros[position]
        holder.nombre.text = logro.nombre
        holder.estado.text = if (logro.desbloqueado) "Desbloqueado" else "Bloqueado"

        holder.icono.setImageResource(
            if (logro.desbloqueado) R.mipmap.trofeo_conseguido
            else R.mipmap.trofeo_bloqueado
        )
    }

    override fun getItemCount(): Int = logros.size

    fun update(newLogros: List<Logros_item>) {
        logros = newLogros
        notifyDataSetChanged()
    }
}
