package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.HtmlCompat
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.StoreItem
import kotlinx.android.synthetic.main.emission_list_item.view.*
import kotlinx.android.synthetic.main.fragment_list.*

class EmissionListAdapter(val context: Context, var purchases: List<Purchase>, val onShowAlternatives: (position: Int) -> List<StoreItem>):  RecyclerView.Adapter<EmissionListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.emission_list_item,
                        parent,
                        false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emission = HtmlCompat.fromHtml("%.2f ".format(purchases[position].emission) + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.purchase.text = purchases[position].toString()
        holder.emission.text = emission
        holder.emissionItemCard.setOnClickListener {
            if (holder.expandableView.isVisible) {
                holder.expandableView.visibility = View.GONE
                holder.showAlternatives.setImageResource(R.drawable.ic_expand_more_black_24dp)
            } else {
                holder.expandableView.visibility = View.VISIBLE
                holder.showAlternatives.setImageResource(R.drawable.ic_expand_less_black_24dp)

                if (holder.alternativesList.isEmpty()) {
                    holder.alternativesList.layoutManager = LinearLayoutManager(context)

                    val alternativesAdapter = AlternativesAdapter(context, purchases[position], onShowAlternatives(position))

                    holder.alternativesList.adapter = alternativesAdapter
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val purchase = view.purchase
        val emission = view.emission
        val showAlternatives = view.showAlternatives
        val expandableView = view.expandableView
        val emissionItemCard = view.emissionItemCard
        val alternativesList = view.alternativesList
    }
}