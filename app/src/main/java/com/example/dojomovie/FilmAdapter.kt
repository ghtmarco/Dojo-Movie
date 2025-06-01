package com.example.dojomovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class FilmAdapter(
    private var films: List<Film>,
    private val onFilmClick: (Film) -> Unit
) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewFilmCover: ImageView = itemView.findViewById(R.id.imageViewFilmCover)
        val textViewFilmTitle: TextView = itemView.findViewById(R.id.textViewFilmTitle)
        val textViewFilmPrice: TextView = itemView.findViewById(R.id.textViewFilmPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]

        holder.textViewFilmTitle.text = film.title

        // Format price to Indonesian Rupiah
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.textViewFilmPrice.text = formatter.format(film.price)

        // Load image using Glide (fallback to placeholder if Glide not available)
        try {
            Glide.with(holder.itemView.context)
                .load(film.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewFilmCover)
        } catch (e: Exception) {
            // Fallback if Glide is not available
            holder.imageViewFilmCover.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.itemView.setOnClickListener {
            onFilmClick(film)
        }
    }

    override fun getItemCount(): Int = films.size

    fun updateFilms(newFilms: List<Film>) {
        films = newFilms
        notifyDataSetChanged()
    }
}