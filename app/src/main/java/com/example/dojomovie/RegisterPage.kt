package com.example.dojomovie
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random // Import untuk generate OTP


class RegisterPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

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
            } else if (password.length < 8) {
                Toast.makeText(this, "Kata Sandi minimal 8 karakter", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Kata Sandi dan Konfirmasi Kata Sandi tidak cocok", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Validasi berhasil. Lanjut proses OTP...", Toast.LENGTH_SHORT).show()
                val otpCode = Random.nextInt(100000, 1000000)

                Toast.makeText(this, "OTP Generated: $otpCode. Navigasi ke OTP Page belum diimplementasikan.", Toast.LENGTH_LONG).show()
            }
        }

        textViewLoginLink.setOnClickListener {
            Toast.makeText(this, "Mengarah ke halaman login...", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }

}