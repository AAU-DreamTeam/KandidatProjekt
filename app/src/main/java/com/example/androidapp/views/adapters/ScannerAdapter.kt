package com.example.androidapp.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.viewmodels.ScannerViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.card_layout_alt.view.*

class ScannerAdapter(var purchases: List<Purchase>, val products: List<String>, val countries: List<String>, val viewModel: ScannerViewModel): RecyclerView.Adapter<ScannerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_alt,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purchase = purchases[position]

        holder.title.setText(purchase.storeItem.receiptText)

        setUpToggleButton(holder, purchase, holder.toggleButton)

        holder.title.doAfterTextChanged {
            viewModel.onTitleChanged(holder.adapterPosition, it.toString())
        }

        holder.product.doAfterTextChanged{
            viewModel.onProductChanged(holder.adapterPosition, it.toString())
        }

        holder.country.doAfterTextChanged {
            viewModel.onCountryChanged(holder.adapterPosition, it.toString())
        }

        holder.amount.doAfterTextChanged {
            viewModel.onQuantityChanged(holder.adapterPosition, it.toString().toInt())
        }

        holder.weight.doAfterTextChanged {
            viewModel.onWeightChanged(holder.adapterPosition, it.toString().toDouble())
        }

        holder.deleteButton.setOnClickListener {
            viewModel.onDeletePurchase(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    private fun setUpToggleButton(holder: ViewHolder, purchase: Purchase, toggleButton: MaterialButtonToggleGroup){
        if (purchase.storeItem.organic){
            toggleButton.check(R.id.btn_organic)
        }

        if (!purchase.storeItem.packaged) {
            toggleButton.check(R.id.btn_packaged)
        }

        toggleButton.addOnButtonCheckedListener(){ _, checkedId, isChecked ->
            when(checkedId) {
                R.id.btn_organic -> viewModel.onOrganicChanged(holder.adapterPosition, isChecked)
                R.id.btn_packaged -> viewModel.onPackagedChanged(holder.adapterPosition, isChecked)
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: MaterialCardView = itemView.cardView
        val toggleButton: MaterialButtonToggleGroup = itemView.findViewById(R.id.toggleButton)
        val deleteButton: Button = itemView.findViewById(R.id.btn_delete)
        val country: AutoCompleteTextView = itemView.findViewById(R.id.countryOption)
        val product: AutoCompleteTextView = itemView.findViewById(R.id.productOption)
        val weight: TextInputEditText = itemView.findViewById(R.id.weight_input)
        val amount: TextInputEditText = itemView.findViewById(R.id.amount_input)
        val title: TextInputEditText = itemView.findViewById(R.id.card_title)

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