package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.StoreItem
import kotlinx.android.synthetic.main.alternative_list_item.view.*

class AlternativesAdapter(val context: Context, val purchase: Purchase, var alternatives: List<StoreItem>): RecyclerView.Adapter<AlternativesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val purchaseAlt = view.purchaseAlt
        val emissionAlt = view.emissionAlt
        val percentage = view.percentage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.alternative_list_item,
                        parent,
                        false
                ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storeItem = alternatives[position]
        val emissionAlt = purchase.weight * storeItem.emissionPerKg
        val tempPurchase = "${purchase.weightToStringKg()}, $storeItem"
        val tempEmission = HtmlCompat.fromHtml("%.3f ".format(emissionAlt) + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        val tempPercentage = "${if (emissionAlt > purchase.emission) "+" else ""} ${"%.3f ".format(((emissionAlt - purchase.emission)/purchase.emission)*100)} %"
        holder.purchaseAlt.text = tempPurchase
        holder.emissionAlt.text = tempEmission
        holder.percentage.text = tempPercentage
    }

    override fun getItemCount(): Int {
        return alternatives.size
    }
}