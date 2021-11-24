package com.example.androidapp.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R

class RecyclerAdapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    private var title = arrayOf("Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4")
    //private var listOfCountries = arrayOf("Spain","Italy","UK", "DK")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        //holder.country.text = listOfCountries[position]
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemTitle: TextView = itemView.findViewById(R.id.weight_text)
        //var country: TextView = itemView.findViewById(R.id.textViewCountryOption)

        
    }



}