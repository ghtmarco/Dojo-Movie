package com.example.dojomovie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OtpPage : AppCompatActivity() {

    private var userPhone: String? = null
    private var userPassword: String? = null
    private var correctOtp: Int = 0
    private var isLogin: Boolean = false // TAMBAH: Flag untuk login vs register
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        dbHelper = DatabaseHelper(this)

        userPhone = intent.getStringExtra("phone")
        userPassword = intent.getStringExtra("password")
        correctOtp = intent.getIntExtra("otp", 0)
        isLogin = intent.getBooleanExtra("is_login", false) // TAMBAH: Check if from login

        val otpInput = findViewById<EditText>(R.id.editTextOtpCode)
        val verifyBtn = findViewById<Button>(R.id.buttonVerifyOtp)
        val instruction = findViewById<TextView>(R.id.textViewOtpInstruction)

        instruction.text = "Masukkan kode OTP yang dikirim ke (650) 555-1212"

        verifyBtn.setOnClickListener {
            val inputOtp = otpInput.text.toString().trim()

            if (inputOtp.isEmpty()) {
                Toast.makeText(this, "Kode OTP harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                val enteredOtp = inputOtp.toIntOrNull()

                if (enteredOtp == null) {
                    Toast.makeText(this, "OTP harus berupa angka", Toast.LENGTH_SHORT).show()
                } else if (enteredOtp == correctOtp) {
                    if (isLogin) {
                        // PERBAIKAN: Jika dari login, langsung ke Home page
                        proceedToHome()
                    } else {
                        // Jika dari register, save user dulu baru ke login
                        saveUserToDatabase()
                    }
                } else {
                    Toast.makeText(this, "Kode OTP salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun proceedToHome() {
        Toast.makeText(this, "OTP verified! Welcome back!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun saveUserToDatabase() {
        if (userPhone != null && userPassword != null) {
            val userId = dbHelper.insertUser(userPhone!!, userPassword!!)

            if (userId > 0) {
                Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_SHORT).show()
        }
    }
}