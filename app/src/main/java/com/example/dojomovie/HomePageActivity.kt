package com.example.dojomovie

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException

class HomePageActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var filmRecycler: RecyclerView
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private lateinit var requestQueue: RequestQueue
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var myMap: GoogleMap
    private lateinit var bottomNavigation: BottomNavigationView
    private val filmList = mutableListOf<Film>()

    private var filmLoadTask: FilmLoadTask? = null
    private lateinit var progressBar: ProgressBar

    private val API_URL = "https://api.npoint.io/66cce8acb8f366d2a508"
    private val DOJO_LOCATION = LatLng(-6.2088, 106.8456)

    private val filmDataWithPosters = mapOf(
        "MV001" to FilmData(
            "Final Fantalion",
            "https://awsimages.detik.net.id/community/media/visual/2025/03/26/final-destination-bloodlines-1742956382525.webp?w=700&q=90",
            75000,
            "A supernatural horror thriller that follows a group of young adults who narrowly escape a catastrophic accident, only to discover that Death itself is hunting them down one by one. With stunning visuals and heart-pounding suspense, this film takes you on a terrifying journey where fate cannot be cheated."
        ),
        "MV002" to FilmData(
            "Kongzilla",
            "https://images-cdn.ubuy.co.in/633f9cdb6c083a361b7bb6c7-ubuy-online-shopping.jpg",
            85000,
            "An epic monster showdown featuring the legendary King Kong and the mighty Godzilla as they face off in an ultimate battle for supremacy. When ancient titans emerge to threaten humanity, only these colossal beasts can determine the fate of our world in this action-packed blockbuster."
        ),
        "MV003" to FilmData(
            "Bond Jampshoot",
            "https://www.postershop.cz/files/e/52494/plakat-james-bond-no-time-to-die-azure-teaser.jpeg",
            95000,
            "In this explosive action thriller, Agent 007 faces his most dangerous mission yet as he infiltrates a high-stakes criminal organization. With cutting-edge gadgets, exotic locations, and intense chase sequences, Bond must save the world from a diabolical plot that threatens global security."
        )
    )

    data class FilmData(
        val title: String,
        val posterUrl: String,
        val price: Int,
        val synopsis: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupViews()
        setupBottomNavigation()
        setupMap()

        requestQueue = Volley.newRequestQueue(this)

        loadFilmsWithAsyncTask()
    }

    private fun setupViews() {
        filmRecycler = findViewById(R.id.recyclerFilms)
        filmRecycler.layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progressBarFilms)

        filmAdapter = FilmAdapter(filmList) { film ->
            val intent = Intent(this, DetailFilmActivity::class.java)
            intent.putExtra("film_id", film.id)
            startActivity(intent)
        }
        filmRecycler.adapter = filmAdapter
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_home

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    filmRecycler.smoothScrollToPosition(0)
                    true
                }
                R.id.nav_history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMap() {
        checkLocationPermission()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        val cameraPosition = CameraPosition.builder()
            .target(DOJO_LOCATION)
            .zoom(17.0f)
            .tilt(45.0f)
            .build()

        myMap.addMarker(
            MarkerOptions()
                .position(DOJO_LOCATION)
                .title("DoJo Movie")
        )

        myMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        myMap.isBuildingsEnabled = true

        Toast.makeText(this, "DoJo Movie location loaded", Toast.LENGTH_SHORT).show()
    }

    private fun loadFilmsWithAsyncTask() {
        filmLoadTask = FilmLoadTask()
        progressBar.visibility = View.VISIBLE
        progressBar.max = 100
        filmLoadTask!!.execute()
    }

    private fun loadFilms() {
        val request = JsonArrayRequest(
            Request.Method.GET, API_URL, null,
            { response ->
                try {
                    val filmsFromJson = parseJSON(response)
                    filmList.clear()
                    filmList.addAll(filmsFromJson)

                    filmsFromJson.forEach { film ->
                        dbHelper.insertFilm(film)
                    }

                    filmAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Films loaded: ${filmList.size}", Toast.LENGTH_SHORT).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "JSON parsing error", Toast.LENGTH_SHORT).show()
                    loadLocalFilms()
                }
            },
            { error ->
                Log.e("Volley error", error.toString())
                Toast.makeText(this, "Network error, loading internal data...", Toast.LENGTH_SHORT).show()
                loadInternalFilmData()
            }
        )

        requestQueue.add(request)
    }

    private fun loadInternalFilmData() {
        val internalFilms = ArrayList<Film>()

        filmDataWithPosters.forEach { (id, filmData) ->
            internalFilms.add(Film(id, filmData.title, filmData.posterUrl, filmData.price, filmData.synopsis))
        }

        filmList.clear()
        filmList.addAll(internalFilms)

        internalFilms.forEach { film ->
            dbHelper.insertFilm(film)
        }

        filmAdapter.notifyDataSetChanged()
        Toast.makeText(this, "${filmList.size} films loaded from internal data", Toast.LENGTH_SHORT).show()
    }

    private fun loadLocalFilms() {
        val localFilms = dbHelper.getAllFilms()
        if (localFilms.isNotEmpty()) {
            filmList.clear()
            filmList.addAll(localFilms)
            filmAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Loaded ${localFilms.size} cached films", Toast.LENGTH_SHORT).show()
        } else {
            loadInternalFilmData()
        }
    }

    private fun parseJSON(jsonArray: JSONArray): ArrayList<Film> {
        val filmList = ArrayList<Film>()
        try {
            for (i in 0 until jsonArray.length()) {
                val filmObject = jsonArray.getJSONObject(i)

                val id = filmObject.getString("id")
                var title = filmObject.getString("title")
                var image = filmObject.getString("image")
                var price = filmObject.getInt("price")
                var synopsis = "An exciting movie experience that will keep you entertained from start to finish."

                filmDataWithPosters[id]?.let { filmData ->
                    title = filmData.title
                    image = filmData.posterUrl
                    price = filmData.price
                    synopsis = filmData.synopsis
                }

                filmList.add(Film(id, title, image, price, synopsis))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return filmList
    }

    inner class FilmLoadTask : android.os.AsyncTask<Void, Int, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                for (i in 0..100 step 10) {
                    Thread.sleep(200)
                    publishProgress(i)
                }
                true
            } catch (e: Exception) {
                false
            }
        }

        override fun onPreExecute() {
            progressBar.visibility = View.VISIBLE
            Toast.makeText(this@HomePageActivity, "Loading films...", Toast.LENGTH_SHORT).show()
            super.onPreExecute()
        }

        override fun onPostExecute(result: Boolean) {
            progressBar.visibility = View.GONE
            if (result) {
                loadFilms()
            } else {
                Toast.makeText(this@HomePageActivity, "Failed to load films", Toast.LENGTH_SHORT).show()
            }
            super.onPostExecute(result)
        }

        override fun onProgressUpdate(vararg values: Int?) {
            if (values.isNotEmpty() && values[0] != null) {
                progressBar.progress = values[0]!!
            }
            super.onProgressUpdate(*values)
        }

        override fun onCancelled() {
            progressBar.visibility = View.GONE
            Toast.makeText(this@HomePageActivity, "Loading cancelled", Toast.LENGTH_SHORT).show()
            super.onCancelled()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        filmLoadTask?.cancel(true)
    }
}