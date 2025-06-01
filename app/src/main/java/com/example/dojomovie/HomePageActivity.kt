package com.example.dojomovie

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomePageActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var filmRecycler: RecyclerView
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private lateinit var volleyQueue: RequestQueue
    private val filmList = mutableListOf<Film>()
    private var map: GoogleMap? = null

    private val API_URL = "https://api.npoint.io/66cce8acb8f366d2a508"
    private val DOJO_LOCATION = LatLng(-6.2088, 106.8456)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)
        volleyQueue = Volley.newRequestQueue(this)

        setupViews()
        setupBottomNavigation()
        setupMap()
        loadFilms()
    }

    private fun setupViews() {
        filmRecycler = findViewById(R.id.recyclerFilms)
        filmAdapter = FilmAdapter(filmList) { film ->
            val intent = Intent(this, DetailFilmActivity::class.java)
            intent.putExtra("film_id", film.id)
            startActivity(intent)
        }

        filmRecycler.layoutManager = LinearLayoutManager(this)
        filmRecycler.adapter = filmAdapter
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        navHome.setOnClickListener {
            // Already on home, do nothing or refresh
            Toast.makeText(this, "You are on Home", Toast.LENGTH_SHORT).show()
        }

        navHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map?.addMarker(
            MarkerOptions()
                .position(DOJO_LOCATION)
                .title("DoJo Movie")
        )

        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(DOJO_LOCATION, 15f))
        Toast.makeText(this, "DoJo Movie location loaded", Toast.LENGTH_SHORT).show()
    }

    private fun loadFilms() {
        val request = JsonArrayRequest(
            Request.Method.GET, API_URL, null,
            { response ->
                filmList.clear()

                for (i in 0 until response.length()) {
                    try {
                        val filmJson = response.getJSONObject(i)
                        val id = filmJson.optString("id", "FILM_$i")
                        val title = filmJson.optString("title", "Unknown Film")
                        val image = filmJson.optString("image", "")
                        val price = filmJson.optInt("price", 50000)

                        val film = Film(id, title, image, price)
                        filmList.add(film)
                        dbHelper.insertFilm(film)

                    } catch (e: Exception) {
                        continue
                    }
                }

                if (filmList.isEmpty()) {
                    loadSampleFilms()
                }

                filmAdapter.updateFilms(filmList)
                Toast.makeText(this, "Films loaded: ${filmList.size}", Toast.LENGTH_SHORT).show()
            },
            { error ->
                loadSampleFilms()
                Toast.makeText(this, "No internet, loading sample data", Toast.LENGTH_SHORT).show()
            }
        )

        volleyQueue.add(request)
    }

    private fun loadSampleFilms() {
        filmList.clear()

        val sampleFilms = listOf(
            Film("MV001", "Avengers Endgame", "https://via.placeholder.com/300x450", 75000),
            Film("MV002", "Spider-Man", "https://via.placeholder.com/300x450", 70000),
            Film("MV003", "Iron Man", "https://via.placeholder.com/300x450", 65000),
            Film("MV004", "Captain America", "https://via.placeholder.com/300x450", 70000),
            Film("MV005", "Thor", "https://via.placeholder.com/300x450", 68000)
        )

        filmList.addAll(sampleFilms)
        sampleFilms.forEach { film ->
            dbHelper.insertFilm(film)
        }

        filmAdapter.updateFilms(filmList)
    }
}