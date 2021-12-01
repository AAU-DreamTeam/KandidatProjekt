package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.StoreItem
import com.example.androidapp.viewmodels.EmissionViewModel
import kotlinx.android.synthetic.main.emission_list_item.view.*

class EmissionListAdapter(val context: Context, var purchases: List<Purchase>, val viewModel: EmissionViewModel):  RecyclerView.Adapter<EmissionListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.emission_list_item,
                        parent,
                        false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emission = HtmlCompat.fromHtml("%.3f ".format(purchases[position].emission) + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.purchase.text = purchases[position].toString()
        holder.emission.text = emission

        holder.emissionItemCard.setOnClickListener {
            if (holder.expandableView.isVisible) {
                holder.hideAlternatives()
            } else {
                holder.showAlternatives()

                if (!holder.alternativesGot) {
                    holder.setUpAlternatives(context, purchases[position], viewModel.loadAlternatives(context, holder.adapterPosition))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return purchases.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val purchase: TextView = view.purchase
        val emission: TextView = view.emission
        val iconShowHideAlternatives: ImageView = view.iconShowHideAlternatives
        val expandableView: LinearLayout = view.expandableView
        val emissionItemCard: CardView = view.emissionItemCard
        val alternativesRV: RecyclerView = view.alternativesRV
        var alternativesGot = false

        fun hideAlternatives(){
            expandableView.visibility = View.GONE
            iconShowHideAlternatives.setImageResource(R.drawable.ic_expand_more_black_24dp)
        }

        fun showAlternatives(){
            expandableView.visibility = View.VISIBLE
            iconShowHideAlternatives.setImageResource(R.drawable.ic_expand_less_black_24dp)
        }

        fun setUpAlternatives(context: Context, purchase: Purchase, alternatives: List<StoreItem>){
            val alternativesAdapter = AlternativesAdapter(context, purchase, alternatives)

            alternativesRV.layoutManager = LinearLayoutManager(context)
            alternativesRV.adapter = alternativesAdapter
            alternativesGot = true
        }
    }
}