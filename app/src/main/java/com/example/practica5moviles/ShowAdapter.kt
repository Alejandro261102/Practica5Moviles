package com.example.practica5moviles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practica5moviles.data.TvShow
import com.example.practica5moviles.databinding.ItemShowBinding

class ShowAdapter(
    private val onFavoriteClick: (TvShow) -> Unit
) : RecyclerView.Adapter<ShowAdapter.ShowViewHolder>() {

    private var shows = listOf<TvShow>()

    fun submitList(newList: List<TvShow>) {
        shows = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val show = shows[position]
        holder.binding.tvTitle.text = show.name
        holder.binding.tvLanguage.text = show.language ?: "Desconocido"

        // Cambiar texto del botón según estado
        holder.binding.btnFavorite.text = if (show.isFavorite) "Quitar Favorito" else "Agregar a Favoritos"

        // Color visual opcional
        holder.binding.btnFavorite.setBackgroundColor(
            if (show.isFavorite) 0xFFFFEB3B.toInt() else 0xFF2196F3.toInt()
        )

        holder.binding.btnFavorite.setOnClickListener {
            onFavoriteClick(show)
        }
    }

    override fun getItemCount() = shows.size

    class ShowViewHolder(val binding: ItemShowBinding) : RecyclerView.ViewHolder(binding.root)
}