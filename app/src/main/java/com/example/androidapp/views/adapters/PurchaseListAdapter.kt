package com.example.androidapp.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.StoreItem
import com.example.androidapp.viewmodels.AlternativesViewModel
import com.example.androidapp.views.AlternativesView
import com.example.androidapp.views.ScannerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.purchase_list_item.view.*
import kotlinx.android.synthetic.main.trip_list_item.view.*

class PurchaseListAdapter(val context: AppCompatActivity, var purchases: List<Purchase>):  RecyclerView.Adapter<PurchaseListAdapter.ViewHolder>(){
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
        val storeItemEmission = purchase.storeItem.emissionPerKg
        val storeItemAltEmission = purchase.storeItem.altEmission.second

        holder.productTV.text = purchase.storeItem.product.name
        holder.emissionTV.text = purchase.emissionToString()
        holder.emissionPerKgTV.text = purchase.storeItem.emissionToString()
        holder.organicTV.text = if (purchase.storeItem.organic) "Ja" else "Nej"
        holder.packagedTV.text = if (purchase.storeItem.packaged) "Nej" else "Ja"
        holder.countryTV.text = purchase.storeItem.country.name
        holder.weightTV.text = purchase.weightToStringG()

        if (storeItemEmission == storeItemAltEmission) {
            holder.alternativeHeaderTV.text = "Der findes ingen bedre alternativer"
            holder.buttonAlternatives.visibility = View.GONE
        } else {
            holder.alternativeHeaderTV.text = "Der findes et alternativ som er"
            holder.buttonAlternatives.text = "${purchase.storeItem.altEmissionDifference()}% BEDRE"

            holder.buttonAlternatives.setOnClickListener{
                AlternativesViewModel.storeItem = purchase.storeItem
                it.context.startActivity(Intent(context, AlternativesView::class.java))
            }

        }
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val alternativeHeaderTV: TextView = view.alternativeHeaderTV
        val productTV: TextView = view.productTV
        val emissionTV: TextView = view.emissionTV
        val emissionPerKgTV: TextView = view.emissionPerKgTV
        val organicTV: TextView = view.organicTV
        val packagedTV: TextView = view.packagedTV
        val countryTV: TextView = view.countryTV
        val weightTV: TextView = view.weightTV
        val buttonAlternatives: MaterialButton = view.btnAlternatives
    }
}