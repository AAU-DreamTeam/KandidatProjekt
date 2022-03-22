package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.Trip
import kotlinx.android.synthetic.main.trip_list_item.view.*

class TripListAdapter(val context: Context, var trips: List<Trip>):  RecyclerView.Adapter<TripListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.trip_list_item,
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tripHeader.text = trips[position].prettyTimestamp()

        if (position == 0) {
            holder.setUpPurchases(context, trips[position].purchases)
            holder.showPurchases()
        } else {
            holder.hidePurchases()
        }

        holder.tripCard.setOnClickListener {
            if (holder.purchaseListRV.isVisible) {
                holder.hidePurchases()
            } else {
                holder.setUpPurchases(context, trips[position].purchases)
                holder.showPurchases()
            }
        }
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tripHeader: TextView = view.tripHeader
        val tripCard: CardView = view.tripCard
        val purchaseListRV: RecyclerView = view.purchaseListRV
        val collapseIcon: ImageView = view.collapseIcon
        var purchasesLoaded = false

        fun hidePurchases(){
            purchaseListRV.visibility = View.GONE
            collapseIcon.setImageResource(R.drawable.ic_expand_more_black_24dp)
        }

        fun showPurchases(){
            purchaseListRV.visibility = View.VISIBLE
            collapseIcon.setImageResource(R.drawable.ic_expand_less_black_24dp)
        }

        fun setUpPurchases(context: Context, purchases: List<Purchase>){
            if (!purchasesLoaded) {
                purchaseListRV.layoutManager = LinearLayoutManager(context)
                purchaseListRV.adapter = PurchaseListAdapter(context, purchases)
                purchasesLoaded = true
            }
        }
    }
}