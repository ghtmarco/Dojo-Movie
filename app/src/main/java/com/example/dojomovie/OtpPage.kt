package com.example.dojomovie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OtpPage : AppCompatActivity() {

    private var receivedPhoneNumber: String? = null
    private var receivedPassword: String? = null
    private var generatedOtpCode: Int = 0
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        databaseHelper = DatabaseHelper(this)

        receivedPhoneNumber = intent.getStringExtra("phone_number")
        receivedPassword = intent.getStringExtra("password")
        generatedOtpCode = intent.getIntExtra("otp_code", 0)

        val editTextOtpCode = findViewById<EditText>(R.id.editTextOtpCode)
        val buttonVerifyOtp = findViewById<Button>(R.id.buttonVerifyOtp)
        val textViewOtpInstruction = findViewById<TextView>(R.id.textViewOtpInstruction)

        // Update instruction text to show phone number
        textViewOtpInstruction.text = "Masukkan kode OTP yang telah dikirim ke $receivedPhoneNumber"

        buttonVerifyOtp.setOnClickListener {
            val enteredOtpText = editTextOtpCode.text.toString().trim()

            if (enteredOtpText.isEmpty()) {
                Toast.makeText(this, "Kode OTP harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                val enteredOtpCode = enteredOtpText.toIntOrNull()

                if (enteredOtpCode == null) {
                    Toast.makeText(this, "Input OTP tidak valid", Toast.LENGTH_SHORT).show()
                } else if (enteredOtpCode == generatedOtpCode) {
                    // OTP verified successfully, save user to database
                    if (receivedPhoneNumber != null && receivedPassword != null) {
                        val userId = databaseHelper.insertUser(receivedPhoneNumber!!, receivedPassword!!)

                        if (userId > 0) {
                            Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, LoginPage::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Gagal menyimpan data pengguna", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Kesalahan: Data pengguna tidak lengkap", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Kode OTP salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}