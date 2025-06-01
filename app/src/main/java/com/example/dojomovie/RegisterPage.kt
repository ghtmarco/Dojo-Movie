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
    private val SMS_PERMISSION = 101
    private val TARGET_PHONE = "6505551212"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)
        checkSmsPermission()

        val phoneInput = findViewById<EditText>(R.id.editTextRegisterPhoneNumber)
        val passwordInput = findViewById<EditText>(R.id.editTextRegisterPassword)
        val confirmInput = findViewById<EditText>(R.id.editTextConfirmPassword)
        val registerBtn = findViewById<Button>(R.id.buttonRegister)
        val loginLink = findViewById<TextView>(R.id.textViewLoginLink)

        registerBtn.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirm = confirmInput.text.toString().trim()

            if (phone.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else if (dbHelper.isPhoneRegistered(phone)) {
                Toast.makeText(this, "Nomor telepon sudah terdaftar", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show()
            } else if (password != confirm) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            } else {
                val otpCode = Random.nextInt(100000, 1000000)
                sendSmsOtp(otpCode)

                val intent = Intent(this, OtpPage::class.java)
                intent.putExtra("phone", phone)
                intent.putExtra("password", password)
                intent.putExtra("otp", otpCode)
                startActivity(intent)
                finish()
            }
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

    private fun sendSmsOtp(otpCode: Int) {
        Thread {
            try {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {

                    val smsManager = SmsManager.getDefault()
                    val message = "DoJo Movie OTP: $otpCode"

                    smsManager.sendTextMessage(TARGET_PHONE, null, message, null, null)

                    runOnUiThread {
                        Toast.makeText(this, "OTP dikirim ke $TARGET_PHONE", Toast.LENGTH_LONG).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "OTP Code: $otpCode", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "OTP Code: $otpCode", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
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