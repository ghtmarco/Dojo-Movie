package com.example.dojomovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.*

data class Film(
    val id: String,
    val title: String,
    val image: String,
    val price: Int
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

        // Format price in Indonesian Rupiah
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.filmPrice.text = formatter.format(film.price)

        // PERBAIKAN: Handle movie poster images from URL
        loadMoviePoster(holder.filmCover, film.image)

        holder.itemView.setOnClickListener {
            onClick(film)
        }
    }

    private fun loadMoviePoster(imageView: ImageView, imageUrl: String) {
        try {
            // Check if we have a valid URL (not empty and not placeholder)
            if (imageUrl.isNotEmpty() &&
                imageUrl != "pathToImage" &&
                (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {

                // Load image from URL with Glide - optimized for movie posters
                Glide.with(imageView.context)
                    .load(imageUrl)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.ic_movie_placeholder)
                            .error(R.drawable.ic_movie_placeholder)
                    )
                    .into(imageView)
            } else {
                // Use placeholder for invalid URLs
                imageView.setImageResource(R.drawable.ic_movie_placeholder)
            }
        } catch (e: Exception) {
            // Fallback to placeholder on any error
            imageView.setImageResource(R.drawable.ic_movie_placeholder)
        }
    }

    override fun getItemCount(): Int = filmList.size

    fun updateFilms(newFilms: List<Film>) {
        filmList = newFilms
        notifyDataSetChanged()
    }
}