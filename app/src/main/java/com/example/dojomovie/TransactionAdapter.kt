wpackage com.example.dojomovie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

data class Transaction(
    val id: Int,
    val userId: Int,
    val filmId: String,
    val quantity: Int,
    val filmTitle: String = "",
    val filmPrice: Int = 0
)

class TransactionAdapter(
    private var transList: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filmTitle: TextView = itemView.findViewById(R.id.textTransFilmTitle)
        val filmPrice: TextView = itemView.findViewById(R.id.textTransFilmPrice)
        val quantity: TextView = itemView.findViewById(R.id.textTransQuantity)
        val totalPrice: TextView = itemView.findViewById(R.id.textTransTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val trans = transList[position]

        holder.filmTitle.text = trans.filmTitle
        holder.quantity.text = "Quantity: ${trans.quantity}"

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.filmPrice.text = "Price: ${formatter.format(trans.filmPrice)}"

        val total = trans.filmPrice * trans.quantity
        holder.totalPrice.text = "Total: ${formatter.format(total)}"
    }

    override fun getItemCount(): Int = transList.size

    fun updateTransactions(newTrans: List<Transaction>) {
        transList = newTrans
        notifyDataSetChanged()
    }
}