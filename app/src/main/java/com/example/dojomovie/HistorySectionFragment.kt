package com.example.dojomovie

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorySectionFragment : Fragment() {

    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var textViewEmptyHistory: TextView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private val transactions = mutableListOf<Transaction>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_section, container, false)

        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory)
        textViewEmptyHistory = view.findViewById(R.id.textViewEmptyHistory)

        databaseHelper = DatabaseHelper(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("DoJoMoviePrefs",
            android.content.Context.MODE_PRIVATE)

        setupRecyclerView()
        loadTransactionHistory()

        return view
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(transactions)
        recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewHistory.adapter = transactionAdapter
    }

    private fun loadTransactionHistory() {
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            val userTransactions = databaseHelper.getUserTransactions(userId)
            transactions.clear()
            transactions.addAll(userTransactions)

            if (transactions.isEmpty()) {
                recyclerViewHistory.visibility = View.GONE
                textViewEmptyHistory.visibility = View.VISIBLE
            } else {
                recyclerViewHistory.visibility = View.VISIBLE
                textViewEmptyHistory.visibility = View.GONE
                transactionAdapter.updateTransactions(transactions)
            }
        } else {
            recyclerViewHistory.visibility = View.GONE
            textViewEmptyHistory.visibility = View.VISIBLE
            textViewEmptyHistory.text = "Please login to view transaction history"
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload transaction history when fragment becomes visible
        loadTransactionHistory()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistorySectionFragment()
    }
}