package com.example.dojomovie

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class DetailFilmActivity : AppCompatActivity() {

    private lateinit var filmCover: ImageView
    private lateinit var filmBackdrop: ImageView
    private lateinit var filmTitle: TextView
    private lateinit var filmPrice: TextView
    private lateinit var filmSynopsis: TextView
    private lateinit var quantityInput: EditText
    private lateinit var totalPrice: TextView
    private lateinit var buyBtn: Button
    private lateinit var toolbar: Toolbar

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private var currentFilm: Film? = null
    private var currentUserId: Int = -1

    private var isNavigating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_film)

        initViews()
        setupToolbar()
        initDatabase()
        setupBackPressedCallback()

        val filmId = intent.getStringExtra("film_id")

        if (filmId != null && filmId.isNotEmpty()) {
            loadFilmDetail(filmId)
        } else {
            Toast.makeText(this, "Film not found", Toast.LENGTH_SHORT).show()
            navigateBack()
        }

        setupQuantityListener()
        setupBuyButton()
    }

    private fun setupBackPressedCallback() {
        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                Log.d("DetailFilm", "OnBackPressedCallback triggered")
                navigateBack()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun navigateBack() {
        if (isNavigating) {
            Log.d("DetailFilm", "Already navigating, ignoring")
            return
        }

        isNavigating = true
        Log.d("DetailFilm", "Navigating back...")

        try {
            finish()
        } catch (e: Exception) {
            Log.e("DetailFilm", "Error during navigation: ${e.message}")
            isNavigating = false
        }
    }

    private fun initViews() {
        filmCover = findViewById(R.id.imageViewFilmCover)
        filmBackdrop = findViewById(R.id.imageViewFilmBackdrop)
        filmTitle = findViewById(R.id.textViewFilmTitle)
        filmPrice = findViewById(R.id.textViewFilmPrice)
        filmSynopsis = findViewById(R.id.textViewSynopsis)
        quantityInput = findViewById(R.id.editTextQuantity)
        totalPrice = findViewById(R.id.textViewTotalPrice)
        buyBtn = findViewById(R.id.buttonBuy)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        try {
            Log.d("DetailFilm", "Setting up toolbar...")

            setSupportActionBar(toolbar)

            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }

            toolbar.post {
                toolbar.setNavigationOnClickListener {
                    Log.d("DetailFilm", "Toolbar navigation clicked")
                    navigateBack()
                }
            }

            Log.d("DetailFilm", "Toolbar setup completed successfully")

        } catch (e: Exception) {
            Log.e("DetailFilm", "Error setting up toolbar: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun initDatabase() {
        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)
        currentUserId = prefs.getInt("user_id", -1)
    }

    private fun loadFilmDetail(filmId: String) {
        currentFilm = dbHelper.getFilmById(filmId)

        if (currentFilm != null) {
            displayFilmDetail(currentFilm!!)
        } else {
            Toast.makeText(this, "Film not found in database", Toast.LENGTH_SHORT).show()
            navigateBack()
        }
    }

    private fun displayFilmDetail(film: Film) {
        filmTitle.text = film.title

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        filmPrice.text = formatter.format(film.price)

        filmSynopsis.text = film.synopsis

        loadFilmImage(film.image)

        updateTotalPrice()
    }

    private fun loadFilmImage(imageUrl: String) {
        try {
            if (imageUrl.isNotEmpty() && imageUrl != "pathToImage") {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(filmCover)

                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(filmBackdrop)
            } else {
                filmCover.setImageResource(R.drawable.ic_launcher_foreground)
                filmBackdrop.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } catch (e: Exception) {
            Log.e("DetailFilm", "Error loading image: ${e.message}")
            filmCover.setImageResource(R.drawable.ic_launcher_foreground)
            filmBackdrop.setImageResource(R.drawable.ic_launcher_foreground)
        }
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
        val quantity = quantityText.toIntOrNull() ?: 1

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
                navigateBack()
            } else {
                Toast.makeText(this, "Purchase failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("DetailFilm", "onSupportNavigateUp called")
        navigateBack()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d("DetailFilm", "Home menu item selected")
                navigateBack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}