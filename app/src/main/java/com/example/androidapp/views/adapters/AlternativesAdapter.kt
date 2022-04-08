package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.StoreItem
import kotlinx.android.synthetic.main.alternative_list_item.view.*

class AlternativesAdapter(val context: Context, val storeItem: StoreItem, var alternatives: List<StoreItem>): RecyclerView.Adapter<AlternativesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productTV = view.productTV
        val differenceTV = view.differenceTV
        val emissionTV = view.emissionTV
        val organicTV = view.organicTV
        val packagedTV = view.packagedTV
        val countryTV = view.countryTV
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
        val item = alternatives[position]
        val emission = HtmlCompat.fromHtml("%.1f ".format(item.emissionPerKg).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.productTV.text = item.product.name
        holder.differenceTV.text = "${(((storeItem.emissionPerKg - item.emissionPerKg) / storeItem.emissionPerKg) * 100).toInt()}% bedre"
        holder.emissionTV.text = emission
        holder.organicTV.text = if (item.organic) "Ja" else "Nej"
        holder.packagedTV.text = if (item.packaged) "Nej" else "Ja"
        holder.countryTV.text = item.country.name
    }

    override fun getItemCount(): Int {
        return alternatives.size
    }
}