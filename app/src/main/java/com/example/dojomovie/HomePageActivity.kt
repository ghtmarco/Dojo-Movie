package com.example.dojomovie

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import org.json.JSONArray
import org.json.JSONException

class HomePageActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var filmRecycler: RecyclerView
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private lateinit var requestQueue: RequestQueue
    private val filmList = mutableListOf<Film>()
    private var map: GoogleMap? = null

    private val API_URL = "https://api.npoint.io/66cce8acb8f366d2a508"
    private val DOJO_LOCATION = LatLng(-6.2088, 106.8456)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)

        setupViews()
        setupBottomNavigation()
        setupMap()

        // Inisialisasi requestQueue dan load films - ikuti cara dosen
        requestQueue = Volley.newRequestQueue(this)
        loadFilms()
    }

    private fun setupViews() {
        filmRecycler = findViewById(R.id.recyclerFilms)
        filmRecycler.layoutManager = LinearLayoutManager(this)

        filmAdapter = FilmAdapter(filmList) { film ->
            val intent = Intent(this, DetailFilmActivity::class.java)
            intent.putExtra("film_id", film.id)
            startActivity(intent)
        }
        filmRecycler.adapter = filmAdapter
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        navHome.setOnClickListener {
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

    // Ikuti struktur demo dosen - mirip dengan MainActivity.kt dosen
    private fun loadFilms() {
        val request = JsonArrayRequest(
            Request.Method.GET, API_URL, null,
            { response ->
                try {
                    val filmsFromJson = parseJSON(response)
                    filmList.clear()
                    filmList.addAll(filmsFromJson)

                    // Simpan ke database
                    filmsFromJson.forEach { film ->
                        dbHelper.insertFilm(film)
                    }

                    filmAdapter.notifyDataSetChanged() // Sesuai cara dosen
                    Toast.makeText(this, "Films loaded: ${filmList.size}", Toast.LENGTH_SHORT).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "JSON parsing error", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Volley error", error.toString()) // Log error sesuai dosen
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(request)
    }

    // Parsing JSON sesuai struktur demo dosen
    private fun parseJSON(jsonArray: JSONArray): ArrayList<Film> {
        val filmList = ArrayList<Film>()
        try {
            for (i in 0 until jsonArray.length()) {
                val filmObject = jsonArray.getJSONObject(i)

                val id = filmObject.getString("id")
                val title = filmObject.getString("title")
                val image = filmObject.getString("image")
                val price = filmObject.getInt("price")

                filmList.add(Film(id, title, image, price))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return filmList
    }
}