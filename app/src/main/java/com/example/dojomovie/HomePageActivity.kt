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

    // AsyncTask dan Progress Bar
    private var filmLoadTask: FilmLoadTask? = null
    private lateinit var progressBar: ProgressBar

    private val API_URL = "https://api.npoint.io/66cce8acb8f366d2a508"
    private val DOJO_LOCATION = LatLng(-6.2088, 106.8456)

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

        // Load films with AsyncTask
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
                    // Already on home, do nothing or scroll to top
                    filmRecycler.smoothScrollToPosition(0)
                    true
                }
                R.id.nav_history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
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
                }
            },
            { error ->
                Log.e("Volley error", error.toString())
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(request)
    }

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

    // AsyncTask inner class
    inner class FilmLoadTask : android.os.AsyncTask<Void, Int, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                // Simulate loading process with progress
                for (i in 0..100 step 10) {
                    Thread.sleep(200) // Simulate work
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
                loadFilms() // Load actual data after simulation
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