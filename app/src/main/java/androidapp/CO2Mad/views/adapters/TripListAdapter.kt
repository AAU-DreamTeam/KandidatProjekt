package androidapp.CO2Mad.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidapp.CO2Mad.R
import androidapp.CO2Mad.models.Purchase
import androidapp.CO2Mad.models.Trip
import androidapp.CO2Mad.viewmodels.EmissionViewModel
import kotlinx.android.synthetic.main.trip_list_item.view.*

class TripListAdapter(val context: AppCompatActivity, val viewModel: EmissionViewModel, var trips: List<Trip>):  RecyclerView.Adapter<TripListAdapter.ViewHolder>(){
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
            setUpPurchases(holder, context, trips[position].purchases)
            showPurchases(holder)
        } else {
            hidePurchases(holder)
        }

        holder.tripCard.setOnClickListener {
            if (holder.purchaseListRV.isVisible) {
                hidePurchases(holder)
            } else {
                setUpPurchases(holder, context, trips[holder.bindingAdapterPosition].purchases)
                showPurchases(holder)
            }
        }
    }

    private fun hidePurchases(holder: ViewHolder){
        holder.purchaseListRV.visibility = View.GONE
        holder.collapseIcon.setImageResource(R.drawable.ic_expand_more_black_24dp)
    }

    private fun showPurchases(holder: ViewHolder){
        holder.purchaseListRV.visibility = View.VISIBLE
        holder.collapseIcon.setImageResource(R.drawable.ic_expand_less_black_24dp)
    }

    private fun setUpPurchases(holder: ViewHolder, context: AppCompatActivity, purchases: List<Purchase>){
        if (!holder.purchasesLoaded) {
            holder.purchaseListRV.layoutManager = LinearLayoutManager(context)
            holder.purchaseListRV.adapter = PurchaseListAdapter(context, viewModel, this, holder, purchases)
            holder.purchasesLoaded = true
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
    }
}