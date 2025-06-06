package com.example.dojomovie

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
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

class LoginPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: SharedPreferences
    private val SMS_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("DoJoMoviePrefs", MODE_PRIVATE)

        val phoneInput = findViewById<EditText>(R.id.editTextPhoneNumber)
        val passwordInput = findViewById<EditText>(R.id.editTextPassword)
        val loginBtn = findViewById<Button>(R.id.buttonLogin)
        val registerLink = findViewById<TextView>(R.id.textViewRegisterLink)

        checkSmsPermission()

        loginBtn.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (phone.isEmpty()) {
                Toast.makeText(this, "Phone number must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Password must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = dbHelper.checkUserLogin(phone, password)

            if (userId != null) {
                with(prefs.edit()) {
                    putInt("user_id", userId)
                    putString("phone_number", phone)
                    putBoolean("is_logged_in", true)
                    apply()
                }

                val otpCode = Random.nextInt(100000, 999999)
                sendLoginOtpAndRedirect(otpCode, phone, password, userId)
            } else {
                Toast.makeText(this, "Phone number and password are not registered", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION)
        }
    }

    private fun getSmsManager(): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
    }

    private fun sendLoginOtpAndRedirect(otpCode: Int, phone: String, password: String, userId: Int) {
        try {
            val message = "DoJo Movie Login OTP: $otpCode"

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

                val smsManager = getSmsManager()
                smsManager.sendTextMessage(phone, null, message, null, null)
                Toast.makeText(this, "Login OTP sent to $phone", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Demo: Login OTP Code is $otpCode", Toast.LENGTH_LONG).show()
            }

            val intent = Intent(this, OtpPage::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("otp", otpCode)
            intent.putExtra("is_login", true)
            intent.putExtra("user_id", userId)
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Demo: Login OTP Code is $otpCode", Toast.LENGTH_LONG).show()

            val intent = Intent(this, OtpPage::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("otp", otpCode)
            intent.putExtra("is_login", true)
            intent.putExtra("user_id", userId)
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