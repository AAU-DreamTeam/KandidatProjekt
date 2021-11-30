package com.example.androidapp.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.models.Country
import com.example.androidapp.data.models.Product
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.viewmodels.ScannerViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.card_layout_alt.view.*

class ScannerAdapter(var purchases: List<Purchase>,
                     val products: List<Product>,
                     val countries: List<Country>,
                     private val viewModel: ScannerViewModel): RecyclerView.Adapter<ScannerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_alt, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purchase = purchases[position]

        setUpTitle(holder, purchase)
        setUpToggleButton(holder, purchase)
        setUpProductDropdown(holder)
        setUpCountryDropdown(holder)
        setUpAmountField(holder, purchase)
        setUpWeightField(holder, purchase)

        holder.deleteButton.setOnClickListener {
            viewModel.onDeletePurchase(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    private fun setUpTitle(holder: ViewHolder, purchase: Purchase){
        holder.title.setText(purchase.storeItem.receiptText.toUpperCase())

        holder.title.doAfterTextChanged {
            if (holder.title.text!!.isEmpty()) {
                holder.title.error = "Indtast tekst"
            } else {
                viewModel.onTitleChanged(holder.adapterPosition, it.toString())
            }
        }
    }

    private fun setUpToggleButton(holder: ViewHolder, purchase: Purchase){
        if (purchase.storeItem.organic){
            holder.toggleButton.check(R.id.btn_organic)
        }

        if (!purchase.storeItem.packaged) {
            holder.toggleButton.check(R.id.btn_packaged)
        }

        holder.toggleButton.addOnButtonCheckedListener(){ _, checkedId, isChecked ->
            when(checkedId) {
                R.id.btn_organic -> viewModel.onOrganicChanged(holder.adapterPosition, isChecked)
                R.id.btn_packaged -> viewModel.onPackagedChanged(holder.adapterPosition, isChecked)
            }
        }
    }

    private fun setUpProductDropdown(holder: ViewHolder) {
        if (holder.product.text.isEmpty()) {
            holder.product.error = "Vælg produkt"
        }

        holder.product.setOnItemClickListener { parent, _, pos, _ ->
            val product = parent.getItemAtPosition(pos) as Product

            holder.product.error = null

            holder.product.setText(product.name, false)
            viewModel.onProductChanged(holder.adapterPosition, product)
        }
    }

    private fun setUpCountryDropdown(holder: ViewHolder) {
        if (holder.country.text.isEmpty()) {
            holder.country.error = "Vælg land"
        }

        holder.country.setOnItemClickListener { parent, view, pos, id ->
            val country = parent.getItemAtPosition(pos) as Country

            holder.country.error = null

            holder.country.setText(country.name, false)
            viewModel.onCountryChanged(holder.adapterPosition, country)
        }
    }

    private fun setUpAmountField(holder: ViewHolder, purchase: Purchase) {
        if (!purchase.hasValidQuantity()) {
            holder.amount.error = "Indtast antal"
        } else {
            holder.amount.setText(purchase.quantity.toString())
        }

        holder.amount.doAfterTextChanged {
            if (holder.amount.text!!.isEmpty()) {
                holder.amount.error = "Indtast antal"
            } else {
                viewModel.onQuantityChanged(holder.adapterPosition, it.toString().toInt())
            }
        }
    }

    private fun setUpWeightField(holder: ViewHolder, purchase: Purchase){
        if (purchase.storeItem.hasValidWeight()) {
            holder.weight.setText(purchase.storeItem.weight.toString())
        } else {
            holder.weight.error = "Indtast vægt"
        }

        holder.weight.doAfterTextChanged {
            if (holder.weight.text!!.isEmpty()) {
                holder.weight.error = "Indtast vægt"
            } else {
                viewModel.onWeightChanged(holder.adapterPosition, it.toString().toDouble())
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
            val productAdapter = ProductAdapter(itemView.context, R.layout.dropdown_item, products)
            val countryAdapter = CountryAdapter(itemView.context, R.layout.dropdown_item, countries)

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