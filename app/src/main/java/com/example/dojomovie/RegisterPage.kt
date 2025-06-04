package com.example.dojomovie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
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
    private lateinit var smsManager: SmsManager // TAMBAH: Ikuti cara dosen
    private val SMS_PERMISSION = 101
    private val TARGET_PHONE = "6505551212"

    // Views - akan diganti ke view binding nanti jika perlu
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmInput: EditText
    private lateinit var registerBtn: Button
    private lateinit var loginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)
        smsManager = SmsManager.getDefault() // TAMBAH: Init SMS Manager seperti dosen

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

            // PERBAIKAN: Validation seperti demo dosen
            if (phone.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show() // Sama dengan dosen
            } else if (dbHelper.isPhoneRegistered(phone)) {
                Toast.makeText(this, "Phone number already registered", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(this, "Password minimum 8 characters", Toast.LENGTH_SHORT).show()
            } else if (password != confirm) {
                Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show()
            } else {
                val otpCode = Random.nextInt(100000, 1000000)
                checkPermissionAndSendSMS(otpCode, phone, password) // IKUTI naming dosen
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

    // PERBAIKAN: Ikuti function pattern dosen dengan nama yang sama
    private fun checkPermissionAndSendSMS(otpCode: Int, phone: String, password: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION)
        } else {
            // PERBAIKAN: Send SMS langsung seperti demo dosen
            sendSmsOtp(otpCode, phone, password)
        }
    }

    // PERBAIKAN: Simplify SMS sending seperti demo dosen
    private fun sendSmsOtp(otpCode: Int, phone: String, password: String) {
        try {
            val message = "DoJo Movie OTP: $otpCode"

            // IKUTI cara dosen: langsung send SMS tanpa thread
            smsManager.sendTextMessage(TARGET_PHONE, null, message, null, null)

            Toast.makeText(this, "OTP sent to $TARGET_PHONE", Toast.LENGTH_LONG).show()

            // Proceed to OTP page
            val intent = Intent(this, OtpPage::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("otp", otpCode)
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "OTP Code: $otpCode", Toast.LENGTH_LONG).show()

            // Proceed anyway for demo purposes
            val intent = Intent(this, OtpPage::class.java)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("otp", otpCode)
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

    // TAMBAH: Utility function seperti demo dosen untuk UI management
    private fun disableView(view: View, isDisable: Boolean) {
        if (isDisable) {
            view.alpha = 0.5f
            view.isEnabled = false
        } else {
            view.alpha = 1.0f
            view.isEnabled = true
        }
    }
}