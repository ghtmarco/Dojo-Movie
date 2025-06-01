package com.example.dojomovie

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRecycler: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var transAdapter: TransactionAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private val transList = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_page)

        historyRecycler = findViewById(R.id.recyclerHistory)
        emptyText = findViewById(R.id.textEmptyHistory)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)

        setupRecycler()
        setupBottomNavigation()
        loadHistory()
    }

    private fun setupRecycler() {
        transAdapter = TransactionAdapter(transList)
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = transAdapter
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        navHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        navHistory.setOnClickListener {
            Toast.makeText(this, "You are on History", Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadHistory() {
        val userId = prefs.getInt("user_id", -1)

        if (userId != -1) {
            val userTransactions = dbHelper.getUserTransactions(userId)
            transList.clear()
            transList.addAll(userTransactions)

            if (transList.isEmpty()) {
                historyRecycler.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "No transaction history"
            } else {
                historyRecycler.visibility = View.VISIBLE
                emptyText.visibility = View.GONE
                transAdapter.updateTransactions(transList)
            }
        } else {
            historyRecycler.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            emptyText.text = "Please login first"
        }
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }
}