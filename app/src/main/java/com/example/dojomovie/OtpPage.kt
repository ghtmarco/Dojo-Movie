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
    private var isLogin: Boolean = false
    private var userId: Int = -1
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        dbHelper = DatabaseHelper(this)

        userPhone = intent.getStringExtra("phone")
        userPassword = intent.getStringExtra("password")
        correctOtp = intent.getIntExtra("otp", 0)
        isLogin = intent.getBooleanExtra("is_login", false)
        userId = intent.getIntExtra("user_id", -1)

        val otpInput = findViewById<EditText>(R.id.editTextOtpCode)
        val verifyBtn = findViewById<Button>(R.id.buttonVerifyOtp)
        val instruction = findViewById<TextView>(R.id.textViewOtpInstruction)

        instruction.text = "Enter the 6-digit code sent to\n$userPhone"

        verifyBtn.setOnClickListener {
            val inputOtp = otpInput.text.toString().trim()

            if (inputOtp.isEmpty()) {
                Toast.makeText(this, "OTP Code must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val enteredOtp = inputOtp.toIntOrNull()

            if (enteredOtp == null) {
                Toast.makeText(this, "OTP must be numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredOtp == correctOtp) {
                if (isLogin) {
                    proceedToHome()
                } else {
                    saveUserAndGoToLogin()
                }
            } else {
                Toast.makeText(this, "Invalid OTP Code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun proceedToHome() {
        Toast.makeText(this, "OTP verification successful! Welcome!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun saveUserAndGoToLogin() {
        if (userPhone != null && userPassword != null) {
            val newUserId = dbHelper.insertUser(userPhone!!, userPassword!!)

            if (newUserId > 0) {
                Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User data is incomplete", Toast.LENGTH_SHORT).show()
        }
    }
}