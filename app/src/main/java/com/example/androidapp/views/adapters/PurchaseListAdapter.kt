package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.StoreItem
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.purchase_list_item.view.*
import kotlinx.android.synthetic.main.trip_list_item.view.*

class PurchaseListAdapter(val context: Context, var purchases: List<Purchase>):  RecyclerView.Adapter<PurchaseListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.purchase_list_item,
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purchase = purchases[position]
        val emission = HtmlCompat.fromHtml("%.1f ".format(purchase.emission).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.productTV.text = purchase.storeItem.product.name
        holder.emissionTV.text = emission
        holder.organicTV.text = if (purchase.storeItem.organic) "Ja" else "Nej"
        holder.packagedTV.text = if (purchase.storeItem.packaged) "Nej" else "Ja"
        holder.countryTV.text = purchase.storeItem.country.name
        holder.weightTV.text = "${(purchase.weight * 1000).toInt()} g"
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val purchaseCard: CardView = view.purchaseCard
        val productTV: TextView = view.productTV
        val emissionTV: TextView = view.emissionTV
        val organicTV: TextView = view.organicTV
        val packagedTV: TextView = view.packagedTV
        val countryTV: TextView = view.countryTV
        val weightTV: TextView = view.weightTV
        val buttonAlternatives: MaterialButton = view.btnAlternatives
    }
}