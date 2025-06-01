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

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        databaseHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)

        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewRegisterLink = findViewById<TextView>(R.id.textViewRegisterLink)

        buttonLogin.setOnClickListener {
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Nomor Telepon harus diisi", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Kata Sandi harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Check credentials in database
                val userId = databaseHelper.checkUserCredentials(phoneNumber, password)

                if (userId != null) {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                    // Save user session
                    with(sharedPreferences.edit()) {
                        putInt("user_id", userId)
                        putString("phone_number", phoneNumber)
                        putBoolean("is_logged_in", true)
                        apply()
                    }

                    // Redirect to Home Page
                    val intent = Intent(this, HomePageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Nomor Telepon atau Kata Sandi salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        textViewRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }
    }
}