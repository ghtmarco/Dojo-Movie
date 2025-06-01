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

            if (phone.isEmpty()) {
                Toast.makeText(this, "Nomor Telepon harus diisi", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Kata Sandi harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                val userId = dbHelper.checkUserLogin(phone, password)

                if (userId != null) {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                    with(prefs.edit()) {
                        putInt("user_id", userId)
                        putString("phone_number", phone)
                        putBoolean("is_logged_in", true)
                        apply()
                    }

                    val intent = Intent(this, HomePageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Nomor Telepon atau Kata Sandi salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }
    }
}