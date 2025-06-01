package com.example.dojomovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class TransactionAdapter(
    private var transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTransactionFilmTitle: TextView = itemView.findViewById(R.id.textViewTransactionFilmTitle)
        val textViewTransactionFilmPrice: TextView = itemView.findViewById(R.id.textViewTransactionFilmPrice)
        val textViewTransactionQuantity: TextView = itemView.findViewById(R.id.textViewTransactionQuantity)
        val textViewTransactionTotal: TextView = itemView.findViewById(R.id.textViewTransactionTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.textViewTransactionFilmTitle.text = transaction.filmTitle
        holder.textViewTransactionQuantity.text = "Quantity: ${transaction.quantity}"

        // Format price to Indonesian Rupiah
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.textViewTransactionFilmPrice.text = "Price per item: ${formatter.format(transaction.filmPrice)}"

        val totalPrice = transaction.filmPrice * transaction.quantity
        holder.textViewTransactionTotal.text = "Total: ${formatter.format(totalPrice)}"
    }

    override fun getItemCount(): Int = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}