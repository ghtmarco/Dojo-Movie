package com.example.dojomovie

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var phoneText: TextView
    private lateinit var logoutBtn: Button
    private lateinit var prefs: SharedPreferences
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        phoneText = findViewById(R.id.textPhoneNumber)
        logoutBtn = findViewById(R.id.buttonLogout)

        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)
        dbHelper = DatabaseHelper(this)

        loadUserInfo()
        setupLogout()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_profile

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomePageActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserInfo() {
        val userId = prefs.getInt("user_id", -1)

        if (userId != -1) {
            val userPhone = dbHelper.getUserPhone(userId)
            if (userPhone != null) {
                phoneText.text = userPhone

                val phoneDetailText = findViewById<TextView>(R.id.textPhoneNumberDetail)
                phoneDetailText?.text = userPhone
            } else {
                phoneText.text = "Phone not found"
            }
        } else {
            phoneText.text = "User not logged in"
        }
    }

    private fun setupLogout() {
        logoutBtn.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                doLogout()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun doLogout() {
        with(prefs.edit()) {
            clear()
            apply()
        }

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}