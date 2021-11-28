package com.example.androidapp.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.models.Purchase
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.card_layout_alt.view.*

class ScannerAdapter(var purchases: List<Purchase>, val products: List<String>, val countries: List<String>): RecyclerView.Adapter<ScannerAdapter.ViewHolder>() {
    private var title = arrayOf("Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_alt,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(purchases[position].storeItem.receiptText)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: MaterialCardView = itemView.cardView
        val toggleButton: MaterialButtonToggleGroup = itemView.findViewById(R.id.toggleButton)
        val title = itemView.findViewById<TextInputEditText>(R.id.card_title)

        init {
            val productAdapter = ArrayAdapter(itemView.context, R.layout.dropdown_item, products)
            val countryAdapter = ArrayAdapter(itemView.context, R.layout.dropdown_item, countries)

            itemView.productOption.setAdapter(productAdapter)
            itemView.countryOption.setAdapter(countryAdapter)

            toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
                toggleBtnListener()
            }
        }

        private fun toggleBtnListener() {
            // TODO: handle toggle buttom listener
        }
    }
}