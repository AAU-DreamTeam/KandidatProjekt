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
import com.example.androidapp.models.enums.PRODUCT_CATEGORY
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.alternative_list_item.view.*

class AlternativesAdapter(val context: Context, val storeItem: StoreItem, var alternatives: List<StoreItem>): RecyclerView.Adapter<AlternativesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productTV = view.productTV
        val differenceTV = view.differenceTV
        val emissionTV = view.emissionTV
        val organicTV = view.organicTV
        val packagedTV = view.packagedTV
        val countryTV = view.countryTV
        val alternativeRatingBtn : MaterialButton = view.alternativeRatingBtn
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
        val emission = HtmlCompat.fromHtml("%.2f ".format(item.emissionPerKg).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub> pr. kg", HtmlCompat.FROM_HTML_MODE_LEGACY)

        if (storeItem.product.productCategory ==  PRODUCT_CATEGORY.VEGETABLES) {
            holder.productTV.visibility = View.GONE
        } else {
            holder.productTV.text = item.product.name
        }

        holder.differenceTV.text = "${item.difference(storeItem)}% bedre"
        holder.emissionTV.text = emission
        holder.organicTV.text = if (item.organic) "Ja" else "Nej"
        holder.packagedTV.text = if (item.packaged) "Nej" else "Ja"
        holder.countryTV.text = item.country.name
        holder.alternativeRatingBtn.setIconResource(item.rating!!.iconId)
        holder.alternativeRatingBtn.setIconTintResource(item.rating!!.colorId)
    }

    override fun getItemCount(): Int {
        return alternatives.size
    }
}