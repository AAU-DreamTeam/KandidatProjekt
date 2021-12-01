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
import kotlinx.android.synthetic.main.data_list_item.view.*

class DataAdapter(val context: Context, var data: List<StoreItem>): RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.name
        val emission = view.emission
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.data_list_item,
                        parent,
                        false
                ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storeItem = data[position]
        val tempEmission = HtmlCompat.fromHtml("%.3f ".format(storeItem.emissionPerKg) + "kg CO<sub><small><small>2</small></small></sub> pr. kg", HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.name.text = storeItem.toString()
        holder.emission.text = tempEmission
    }

    override fun getItemCount(): Int {
        return data.size
    }
}