package com.example.androidapp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.androidapp.data.models.Country


class CountryAdapter(context: Context, private val resource: Int, private val countries: List<Country>): ArrayAdapter<Country>(context, resource, countries) {

    override fun getItem(position: Int): Country = countries[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = createView(convertView, parent, resource)

        return setUpData(getItem(position), view)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = createView(convertView, parent, android.R.layout.simple_spinner_dropdown_item)

        return setUpData(getItem(position), view)
    }

    private fun createView(convertView: View?, parent: ViewGroup, resource: Int): TextView {
        val context = parent.context
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        return view as TextView
    }

    private fun setUpData(country: Country, view: TextView): TextView {
        view.text = country.name
        return view
    }
}