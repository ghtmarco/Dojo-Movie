package com.example.dojomovie

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)

        val phoneInput = findViewById<EditText>(R.id.editTextPhoneNumber)
        val passwordInput = findViewById<EditText>(R.id.editTextPassword)
        val loginBtn = findViewById<Button>(R.id.buttonLogin)
        val registerLink = findViewById<TextView>(R.id.textViewRegisterLink)

        loginBtn.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // IKUTI SOAL: Validate phone number must be filled
            if (phone.isEmpty()) {
                Toast.makeText(this, "Phone number must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IKUTI SOAL: Validate password must be filled
            if (password.isEmpty()) {
                Toast.makeText(this, "Password must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IKUTI SOAL: Validate phone number and password are registered in database
            val userId = dbHelper.checkUserLogin(phone, password)

            if (userId != null) {
                // Save login session
                with(prefs.edit()) {
                    putInt("user_id", userId)
                    putString("phone_number", phone)
                    putBoolean("is_logged_in", true)
                    apply()
                }

                // IKUTI SOAL: "Redirect the logged-in user to the OTP page"
                val intent = Intent(this, OtpPage::class.java)
                intent.putExtra("phone", phone)
                intent.putExtra("password", password)
                intent.putExtra("otp", 123456) // Dummy OTP untuk login verification
                intent.putExtra("is_login", true) // Flag untuk membedakan login vs register
                intent.putExtra("user_id", userId) // Pass user ID
                startActivity(intent)
                finish()
            } else {
                // IKUTI SOAL: If validation fails, show error message using Toast
                Toast.makeText(this, "Phone number and password are not registered", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }
    }
}