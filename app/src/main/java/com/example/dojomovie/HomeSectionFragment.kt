package com.example.dojomovie

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeSectionFragment : Fragment(), OnMapReadyCallback {

    private lateinit var recyclerViewFilms: RecyclerView
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var requestQueue: RequestQueue
    private val films = mutableListOf<Film>()
    private var googleMap: GoogleMap? = null

    private val dojoMovieLocation = LatLng(-6.2088, 106.8456)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_section, container, false)

        Log.d(TAG, "=== HomeSectionFragment Started ===")

        databaseHelper = DatabaseHelper(requireContext())
        requestQueue = Volley.newRequestQueue(requireContext())

        setupRecyclerView(view)
        setupGoogleMaps()

        if (isNetworkAvailable()) {
            loadFilmsFromApi()
        } else {
            loadFilmsFromDatabase()
            Toast.makeText(requireContext(), "No internet connection. Loading cached data.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun setupRecyclerView(view: View) {
        recyclerViewFilms = view.findViewById(R.id.recyclerViewFilms)
        filmAdapter = FilmAdapter(films) { film ->
            Log.d(TAG, "Film clicked: ${film.title} (ID: ${film.id})")
            val intent = Intent(requireContext(), DetailFilmActivity::class.java)
            intent.putExtra("film_id", film.id)
            startActivity(intent)
        }

        recyclerViewFilms.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFilms.adapter = filmAdapter
    }

    private fun setupGoogleMaps() {
        Log.d(TAG, "Setting up Google Maps")
        try {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up Google Maps: ${e.message}")
        }
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d(TAG, "🗺️ Google Maps is ready!")
        googleMap = map

        try {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(dojoMovieLocation)
                    .title("DoJo Movie")
                    .snippet("DoJo Movie Store Location")
            )

            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(dojoMovieLocation, 15f))
            googleMap?.uiSettings?.isZoomControlsEnabled = true
            googleMap?.uiSettings?.isCompassEnabled = true

            Toast.makeText(requireContext(), "📍 DoJo Movie location loaded", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e(TAG, "Error setting up map marker: ${e.message}")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    private fun loadFilmsFromApi() {
        Log.d(TAG, "Loading films from API")

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, API_URL, null,
            { response ->
                try {
                    parseFilmDataWithPosters(response)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing response: ${e.message}")
                    loadFallbackData()
                }
            },
            { error ->
                Log.e(TAG, "API Error: ${error.message}")
                loadFilmsFromDatabase()
                if (films.isEmpty()) {
                    loadFallbackData()
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "DoJoMovie Android App"
                return headers
            }
        }

        requestQueue.add(jsonArrayRequest)
    }

    private fun parseFilmDataWithPosters(response: org.json.JSONArray) {
        films.clear()

        // 🎬 Enhanced poster mapping
        val posterMap = mapOf(
            "MV001" to "https://images-cdn.ubuy.co.in/633f9cdb6c083a361b7bb6c7-ubuy-online-shopping.jpg",
            "MV002" to "https://www.movieposters.com/cdn/shop/files/final-destination-bloodlines_rakfqp6l.jpg?v=1746032700",
            "MV003" to "https://www.panicposters.com/cdn/shop/products/pp35049-bond-no-time-to-die-poster.jpg?v=1681586490",
            "Kongzilla" to "https://images-cdn.ubuy.co.in/633f9cdb6c083a361b7bb6c7-ubuy-online-shopping.jpg",
            "Final Fantalion" to "https://www.movieposters.com/cdn/shop/files/final-destination-bloodlines_rakfqp6l.jpg?v=1746032700",
            "Bond Jampshoot" to "https://www.panicposters.com/cdn/shop/products/pp35049-bond-no-time-to-die-poster.jpg?v=1681586490"
        )

        for (i in 0 until response.length()) {
            try {
                val filmJson = response.getJSONObject(i)
                val id = filmJson.optString("id", "FILM_$i")
                val title = filmJson.optString("title", "Unknown Title")
                val originalImage = filmJson.optString("image", "")
                val price = filmJson.optInt("price", 50000)

                val enhancedImage = when {
                    posterMap.containsKey(id) -> posterMap[id]!!
                    posterMap.containsKey(title) -> posterMap[title]!!
                    originalImage != "pathToImage" && originalImage.isNotEmpty() -> originalImage
                    else -> "https://via.placeholder.com/300x450/333333/FFFFFF?text=${title.replace(" ", "+")}"
                }

                val film = Film(id, title, enhancedImage, price)
                films.add(film)
                databaseHelper.insertFilm(film)

            } catch (e: Exception) {
                Log.e(TAG, "Error parsing film $i: ${e.message}")
            }
        }

        if (films.isNotEmpty()) {
            filmAdapter.updateFilms(films)
            Toast.makeText(requireContext(), "✅ Loaded ${films.size} films with enhanced posters!", Toast.LENGTH_SHORT).show()
        } else {
            loadFallbackData()
        }
    }

    private fun loadFilmsFromDatabase() {
        try {
            val dbFilms = databaseHelper.getAllFilms()
            films.clear()
            films.addAll(dbFilms)
            filmAdapter.updateFilms(films)

            if (films.isEmpty()) {
                loadFallbackData()
            } else {
                Toast.makeText(requireContext(), "📱 Loaded ${films.size} films from cache", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            loadFallbackData()
        }
    }

    private fun loadFallbackData() {
        films.clear()

        val fallbackFilms = listOf(
            Film("SAMPLE001", "Godzilla vs Kong", "https://m.media-amazon.com/images/M/MV5BZmYzMzU2ZmQtYWQwZC00YmVkLWE5N2ItYjMyMTZiY2RlYjIxXkEyXkFqcGdeQXVyNzI4MDMyMTU@._V1_SX300.jpg", 75000),
            Film("SAMPLE002", "Final Fantasy VII", "https://m.media-amazon.com/images/M/MV5BNzA5ZDNlZWMtM2NhNS00NDJjLTk4NDItYTRmY2EwMWI5MTktXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg", 80000),
            Film("SAMPLE003", "Avengers: Endgame", "https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_SX300.jpg", 70000),
            Film("SAMPLE004", "Spider-Man: No Way Home", "https://m.media-amazon.com/images/M/MV5BZWMyYzFjYTYtNTRjYi00OGExLWE2YzgtOGRmYjAxZTU3NzBi@._V1_SX300.jpg", 75000),
            Film("SAMPLE005", "Doctor Strange", "https://m.media-amazon.com/images/M/MV5BNjgwNzAzNjk1Nl5BMl5BanBnXkFtZTgwMzQ2NjI1OTE@._V1_SX300.jpg", 85000)
        )

        films.addAll(fallbackFilms)
        fallbackFilms.forEach { film ->
            try {
                databaseHelper.insertFilm(film)
            } catch (e: Exception) {
                // Ignore database errors for fallback data
            }
        }

        filmAdapter.updateFilms(films)
        Toast.makeText(requireContext(), "🎬 Loaded sample data (${films.size} films)", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            requestQueue.cancelAll(this)
        } catch (e: Exception) {
            // Ignore
        }
    }

    companion object {
        private const val TAG = "HomeSectionFragment"
        private const val API_URL = "https://api.npoint.io/66cce8acb8f366d2a508"

        @JvmStatic
        fun newInstance() = HomeSectionFragment()
    }
}