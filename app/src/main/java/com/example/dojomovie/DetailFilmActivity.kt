package com.example.dojomovie

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class DetailFilmActivity : AppCompatActivity() {

    private lateinit var imageViewFilmCover: ImageView
    private lateinit var textViewFilmTitle: TextView
    private lateinit var textViewFilmPrice: TextView
    private lateinit var editTextQuantity: EditText
    private lateinit var textViewTotalPrice: TextView
    private lateinit var buttonBuy: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var currentFilm: Film? = null
    private var currentUserId: Int = -1

    companion object {
        private const val TAG = "DetailFilmActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_film)

        // Initialize views
        imageViewFilmCover = findViewById(R.id.imageViewFilmCover)
        textViewFilmTitle = findViewById(R.id.textViewFilmTitle)
        textViewFilmPrice = findViewById(R.id.textViewFilmPrice)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice)
        buttonBuy = findViewById(R.id.buttonBuy)

        databaseHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)

        // Get current user ID
        currentUserId = sharedPreferences.getInt("user_id", -1)

        // 🔧 FIXED: Get String film ID from intent
        val filmId = intent.getStringExtra("film_id")
        Log.d(TAG, "Received film ID: $filmId")

        if (filmId != null && filmId.isNotEmpty()) {
            loadFilmDetail(filmId)
        } else {
            Log.e(TAG, "Error: No film ID provided or empty film ID")
            Toast.makeText(this, "Error: Film not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupQuantityListener()
        setupBuyButton()
    }

    private fun loadFilmDetail(filmId: String) {
        Log.d(TAG, "Loading film detail for ID: $filmId")
        currentFilm = databaseHelper.getFilmById(filmId)

        if (currentFilm != null) {
            Log.d(TAG, "Film found: ${currentFilm!!.title}")
            displayFilmDetail(currentFilm!!)
        } else {
            Log.e(TAG, "Film not found in database for ID: $filmId")
            Toast.makeText(this, "Film not found in database", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayFilmDetail(film: Film) {
        Log.d(TAG, "Displaying film detail: ${film.title}")

        textViewFilmTitle.text = film.title

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        textViewFilmPrice.text = formatter.format(film.price)

        // Load film cover
        try {
            if (film.image.isNotEmpty() && film.image != "pathToImage") {
                Glide.with(this)
                    .load(film.image)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageViewFilmCover)
            } else {
                // Use placeholder for empty or placeholder image paths
                imageViewFilmCover.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error loading image: ${e.message}")
            imageViewFilmCover.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Initialize total price
        updateTotalPrice()
    }

    private fun setupQuantityListener() {
        editTextQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateTotalPrice()
            }
        })
    }

    private fun updateTotalPrice() {
        val quantityText = editTextQuantity.text.toString()
        val quantity = quantityText.toIntOrNull() ?: 0

        val totalPrice = if (currentFilm != null) {
            currentFilm!!.price * quantity
        } else {
            0
        }

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        textViewTotalPrice.text = formatter.format(totalPrice)
    }

    private fun setupBuyButton() {
        buttonBuy.setOnClickListener {
            Log.d(TAG, "Buy button clicked")

            val quantityText = editTextQuantity.text.toString().trim()

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

            Log.d(TAG, "Processing purchase: User $currentUserId, Film ${currentFilm!!.id}, Quantity $quantity")

            // 🔧 FIXED: Insert transaction with String film ID
            val transactionId = databaseHelper.insertTransaction(currentUserId, currentFilm!!.id, quantity)

            if (transactionId > 0) {
                Log.d(TAG, "Purchase successful, transaction ID: $transactionId")

                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                val totalAmount = currentFilm!!.price * quantity

                Toast.makeText(this,
                    "Purchase successful!\n${currentFilm!!.title}\nQuantity: $quantity\nTotal: ${formatter.format(totalAmount)}",
                    Toast.LENGTH_LONG).show()
                finish() // Go back to previous screen
            } else {
                Log.e(TAG, "Purchase failed, transaction ID: $transactionId")
                Toast.makeText(this, "Purchase failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}