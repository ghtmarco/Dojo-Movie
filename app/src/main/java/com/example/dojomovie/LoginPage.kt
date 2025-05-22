package com.example.dojomovie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class LoginPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)



        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewRegisterLink = findViewById<TextView>(R.id.textViewRegisterLink)


        buttonLogin.setOnClickListener {
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val password = editTextPassword.text.toString().trim()


            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Nomor Telepon harus diisi", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Kata Sandi harus diisi", Toast.LENGTH_SHORT).show()
            } else {


                val isValidUser = false
                if (isValidUser) {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()


                } else {

                    Toast.makeText(this, "Nomor Telepon atau Kata Sandi salah", Toast.LENGTH_SHORT).show()
                }
            }
        }


        textViewRegisterLink.setOnClickListener {
            Toast.makeText(this, "Mengarah ke halaman registrasi...", Toast.LENGTH_SHORT).show()

        }
    }

}