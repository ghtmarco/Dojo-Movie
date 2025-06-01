package com.example.dojomovie

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class DetailFilmActivity : AppCompatActivity() {

    private lateinit var filmCover: ImageView
    private lateinit var filmTitle: TextView
    private lateinit var filmPrice: TextView
    private lateinit var quantityInput: EditText
    private lateinit var totalPrice: TextView
    private lateinit var buyBtn: Button

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private var currentFilm: Film? = null
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_film)

        filmCover = findViewById(R.id.imageViewFilmCover)
        filmTitle = findViewById(R.id.textViewFilmTitle)
        filmPrice = findViewById(R.id.textViewFilmPrice)
        quantityInput = findViewById(R.id.editTextQuantity)
        totalPrice = findViewById(R.id.textViewTotalPrice)
        buyBtn = findViewById(R.id.buttonBuy)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)

        currentUserId = prefs.getInt("user_id", -1)

        val filmId = intent.getStringExtra("film_id")

        if (filmId != null && filmId.isNotEmpty()) {
            loadFilmDetail(filmId)
        } else {
            Toast.makeText(this, "Film not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupQuantityListener()
        setupBuyButton()

        supportActionBar?.title = "Film Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadFilmDetail(filmId: String) {
        currentFilm = dbHelper.getFilmById(filmId)

        if (currentFilm != null) {
            displayFilmDetail(currentFilm!!)
        } else {
            Toast.makeText(this, "Film not found in database", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayFilmDetail(film: Film) {
        filmTitle.text = film.title

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        filmPrice.text = formatter.format(film.price)

        try {
            if (film.image.isNotEmpty() && film.image != "pathToImage") {
                Glide.with(this)
                    .load(film.image)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(filmCover)
            } else {
                filmCover.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } catch (e: Exception) {
            filmCover.setImageResource(R.drawable.ic_launcher_foreground)
        }

        updateTotalPrice()
    }

    private fun setupQuantityListener() {
        quantityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateTotalPrice()
            }
        })
    }

    private fun updateTotalPrice() {
        val quantityText = quantityInput.text.toString()
        val quantity = quantityText.toIntOrNull() ?: 0

        val filmPriceValue = currentFilm?.price ?: 0

        val total = filmPriceValue * quantity
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        totalPrice.text = formatter.format(total)
    }

    private fun setupBuyButton() {
        buyBtn.setOnClickListener {
            val quantityText = quantityInput.text.toString().trim()

            if (quantityText.isEmpty()) {
                Toast.makeText(this, "Quantity must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityText.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Quantity must be a number and more than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentUserId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentFilm == null) {
                Toast.makeText(this, "Film data not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transactionId = dbHelper.insertTransaction(currentUserId, currentFilm!!.id, quantity)

            if (transactionId > 0) {
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                val totalAmount = currentFilm!!.price * quantity

                Toast.makeText(this,
                    "Purchase successful!\n${currentFilm!!.title}\nQuantity: $quantity\nTotal: ${formatter.format(totalAmount)}",
                    Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Purchase failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}