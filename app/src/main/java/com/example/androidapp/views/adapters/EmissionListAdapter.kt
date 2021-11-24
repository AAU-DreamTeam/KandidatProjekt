package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.models.Purchase
import kotlinx.android.synthetic.main.emission_list_item.view.*

class EmissionListAdapter(val context: Context, var purchases: List<Purchase>):  RecyclerView.Adapter<EmissionListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.emission_list_item,
                        parent,
                        false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emission = "%.2f KG CO2".format(purchases[position].emission)
        holder.purchase.text = purchases[position].toString()
        holder.emission.text = emission
        holder.emissionItemCard.setOnClickListener {
            if (holder.expandableView.isVisible) {
                holder.expandableView.visibility = View.GONE
                holder.showAlternatives.setImageResource(R.drawable.ic_expand_more_black_24dp)
            } else {
                holder.expandableView.visibility = View.VISIBLE
                holder.showAlternatives.setImageResource(R.drawable.ic_expand_less_black_24dp)
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
    }
}