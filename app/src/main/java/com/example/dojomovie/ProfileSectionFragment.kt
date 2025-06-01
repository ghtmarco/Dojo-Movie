package com.example.dojomovie

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ProfileSectionFragment : Fragment() {

    private lateinit var textViewPhoneNumber: TextView
    private lateinit var buttonLogout: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_section, container, false)

        textViewPhoneNumber = view.findViewById(R.id.textViewPhoneNumber)
        buttonLogout = view.findViewById(R.id.buttonLogout)

        sharedPreferences = requireActivity().getSharedPreferences("DoJoMoviePrefs",
            android.content.Context.MODE_PRIVATE)
        databaseHelper = DatabaseHelper(requireContext())

        loadUserInfo()
        setupLogoutButton()

        return view
    }

    private fun loadUserInfo() {
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            val phoneNumber = databaseHelper.getUserPhoneNumber(userId)
            if (phoneNumber != null) {
                textViewPhoneNumber.text = phoneNumber
            } else {
                textViewPhoneNumber.text = "Phone number not found"
            }
        } else {
            textViewPhoneNumber.text = "User not logged in"
        }
    }

    private fun setupLogoutButton() {
        buttonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        // Clear user session
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Redirect to Login Page
        val intent = Intent(requireContext(), LoginPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileSectionFragment()
    }
}