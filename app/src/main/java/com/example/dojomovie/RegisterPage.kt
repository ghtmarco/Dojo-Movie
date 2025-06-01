package com.example.dojomovie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class RegisterPage : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)

        val editTextRegisterPhoneNumber = findViewById<EditText>(R.id.editTextRegisterPhoneNumber)
        val editTextRegisterPassword = findViewById<EditText>(R.id.editTextRegisterPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val textViewLoginLink = findViewById<TextView>(R.id.textViewLoginLink)

        buttonRegister.setOnClickListener {
            val phoneNumber = editTextRegisterPhoneNumber.text.toString().trim()
            val password = editTextRegisterPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else if (databaseHelper.isPhoneNumberRegistered(phoneNumber)) {
                Toast.makeText(this, "Nomor telepon sudah terdaftar", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(this, "Kata Sandi minimal 8 karakter", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Kata Sandi dan Konfirmasi Kata Sandi tidak cocok", Toast.LENGTH_SHORT).show()
            } else {
                // Generate OTP and show it (simplified for testing)
                val otpCode = Random.nextInt(100000, 1000000)

                // Show OTP in Toast for testing
                Toast.makeText(this,
                    "📱 OTP Code: $otpCode\n\nGunakan kode ini di halaman berikutnya!",
                    Toast.LENGTH_LONG).show()

                // Navigate to OTP page
                val intent = Intent(this, OtpPage::class.java)
                intent.putExtra("phone_number", phoneNumber)
                intent.putExtra("password", password)
                intent.putExtra("otp_code", otpCode)
                startActivity(intent)
                finish()
            }
        }

        textViewLoginLink.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }
}