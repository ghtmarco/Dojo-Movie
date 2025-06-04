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

            if (phone.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.isPhoneRegistered(phone)) {
                Toast.makeText(this, "Phone number must be unique and not registered by other users", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password's length must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Password and confirm password must be the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otpCode = Random.nextInt(100000, 999999)

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

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {

                smsManager.sendTextMessage(phone, null, message, null, null)
                Toast.makeText(this, "OTP sent to $phone", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Demo: OTP Code is $otpCode", Toast.LENGTH_LONG).show()
            }

            val intent = Intent(this, OtpPage::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("otp", otpCode)
            intent.putExtra("is_login", false)
            startActivity(intent)
            finish()

        } catch (e: Exception) {
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