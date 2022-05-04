package androidapp.CO2Mad.views.adapters

import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidapp.CO2Mad.R
import androidapp.CO2Mad.models.Purchase
import androidapp.CO2Mad.viewmodels.AlternativesViewModel
import androidapp.CO2Mad.viewmodels.EmissionViewModel
import androidapp.CO2Mad.views.AlternativesView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.purchase_list_item.view.*

class PurchaseListAdapter(val context: AppCompatActivity, val viewModel: EmissionViewModel, val parent: TripListAdapter, val parentHolder: TripListAdapter.ViewHolder, var purchases: List<Purchase>):  RecyclerView.Adapter<PurchaseListAdapter.ViewHolder>(){
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

        holder.productTV.text = purchase.storeItem.product.name
        holder.emissionTV.text = purchase.emissionToString()
        holder.emissionPerKgTV.text = purchase.storeItem.emissionToString()
        holder.organicTV.text = if (purchase.storeItem.organic) "Ja" else "Nej"
        holder.packagedTV.text = if (purchase.storeItem.packaged) "Nej" else "Ja"
        holder.countryTV.text = purchase.storeItem.country.name
        holder.weightTV.text = purchase.weightToStringG()
        holder.ratingIV.setIconResource(purchase.storeItem.rating!!.iconId)
        holder.ratingIV.setIconTintResource(purchase.storeItem.rating!!.colorId)

        if (purchase.storeItem.altEmissions!!.isEmpty()) {
            holder.alternativeHeaderTV.text = "Der findes ingen bedre alternativer"
            holder.buttonAlternatives.visibility = View.GONE
        } else {
            holder.alternativeHeaderTV.text = "Der findes et alternativ som er"
            holder.buttonAlternatives.text = "${purchase.storeItem.altEmissionDifferenceText()}% BEDRE"

            holder.buttonAlternatives.setOnClickListener{
                AlternativesViewModel.storeItem = purchase.storeItem
                it.context.startActivity(Intent(context, AlternativesView::class.java))
            }
        }

        holder.btnDeletePurchase.setOnClickListener{
            AlertDialog.Builder(context)
                .setTitle("Slet indkøb")
                .setMessage("Er du sikker på at du vil slette dette indkøb?")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    val tripPosition = parentHolder.bindingAdapterPosition

                    if (viewModel.onDeletePurchase(tripPosition, purchases[holder.bindingAdapterPosition])) {
                        parent.notifyItemRemoved(tripPosition)
                    }

                    notifyItemRemoved(holder.bindingAdapterPosition)
                }
                .setNegativeButton(android.R.string.no,null)
                .create()
                .show()
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
        val ratingIV: MaterialButton = view.ratingIV
        val btnDeletePurchase: MaterialButton = view.btnDeletePurchase
    }
}