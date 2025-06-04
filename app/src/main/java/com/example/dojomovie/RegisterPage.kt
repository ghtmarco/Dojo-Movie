package com.example.dojomovie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.random.Random

class RegisterPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var smsManager: SmsManager
    private val SMS_PERMISSION = 101

    // Views
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmInput: EditText
    private lateinit var registerBtn: Button
    private lateinit var loginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)
        smsManager = SmsManager.getDefault()

        initViews()
        setupListeners()
        checkSmsPermission()
    }

    private fun initViews() {
        phoneInput = findViewById(R.id.editTextRegisterPhoneNumber)
        passwordInput = findViewById(R.id.editTextRegisterPassword)
        confirmInput = findViewById(R.id.editTextConfirmPassword)
        registerBtn = findViewById(R.id.buttonRegister)
        loginLink = findViewById(R.id.textViewLoginLink)
    }

    private fun setupListeners() {
        registerBtn.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirm = confirmInput.text.toString().trim()

            // IKUTI SOAL: Validate that all fields must be filled
            if (phone.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IKUTI SOAL: Validate that phone number must be unique and not registered by other users
            if (dbHelper.isPhoneRegistered(phone)) {
                Toast.makeText(this, "Phone number must be unique and not registered by other users", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IKUTI SOAL: Validate that password's length of at least 8 characters
            if (password.length < 8) {
                Toast.makeText(this, "Password's length must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IKUTI SOAL: Validate that password and confirm password is the same
            if (password != confirm) {
                Toast.makeText(this, "Password and confirm password must be the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // IKUTI SOAL: Generate the OTP Code by random six numbers
            val otpCode = Random.nextInt(100000, 999999)

            // IKUTI SOAL: Send it to the user's input phone number and redirect user to OTP Page
            sendOtpAndRedirect(otpCode, phone, password)
        }

        loginLink.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION)
        }
    }

    private fun sendOtpAndRedirect(otpCode: Int, phone: String, password: String) {
        try {
            val message = "DoJo Movie OTP: $otpCode"

            // IKUTI SOAL: Send OTP to user's phone number
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

                // Send SMS to the user's phone number
                smsManager.sendTextMessage(phone, null, message, null, null)
                Toast.makeText(this, "OTP sent to $phone", Toast.LENGTH_LONG).show()
            } else {
                // For demo purposes, show OTP in toast
                Toast.makeText(this, "Demo: OTP Code is $otpCode", Toast.LENGTH_LONG).show()
            }

            // IKUTI SOAL: Redirect user to OTP Page
            val intent = Intent(this, OtpPage::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("otp", otpCode)
            intent.putExtra("is_login", false) // This is registration flow
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            // Fallback: show OTP and proceed anyway
            Toast.makeText(this, "Demo: OTP Code is $otpCode", Toast.LENGTH_LONG).show()

            val intent = Intent(this, OtpPage::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("otp", otpCode)
            intent.putExtra("is_login", false)
            startActivity(intent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}