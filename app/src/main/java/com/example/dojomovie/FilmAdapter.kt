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

data class Film(
    val id: String,
    val title: String,
    val image: String,
    val price: Int,
    val synopsis: String = "An exciting movie experience that will keep you entertained from start to finish."
)

class FilmAdapter(
    private var filmList: List<Film>,
    private val onClick: (Film) -> Unit
) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filmCover: ImageView = itemView.findViewById(R.id.imageFilmCover)
        val filmTitle: TextView = itemView.findViewById(R.id.textFilmTitle)
        val filmPrice: TextView = itemView.findViewById(R.id.textFilmPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = filmList[position]

        holder.filmTitle.text = film.title

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.filmPrice.text = formatter.format(film.price)

        Glide.with(holder.filmCover.context)
            .load(film.image)
            .into(holder.filmCover)

        holder.itemView.setOnClickListener {
            onClick(film)
        }
    }

    override fun getItemCount(): Int = filmList.size

    fun updateFilms(newFilms: List<Film>) {
        filmList = newFilms
        notifyDataSetChanged()
    }
}