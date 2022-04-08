package com.example.androidapp.models

import android.text.Spanned
import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Month
import java.time.Year
import java.util.*

class Purchase(val id: Int,
               val storeItem: StoreItem,
               val calendar: Calendar,
               var quantity: Int){
    var quantityDefault = false
    constructor(storeItem: StoreItem, calendar: Calendar, quantity: Int): this(0, storeItem, calendar, quantity){
        if(storeItem.product.name == "" || storeItem.weight == 0.0 || storeItem.country.name == "" || quantity == 0){
            completed = false
        }
    }
    var completed : Boolean = true

    var weight = storeItem.weight * quantity
        private set
    val emission: Double get() =  storeItem.emissionPerKg * weight
    val week: Int get() = calendar.get(Calendar.WEEK_OF_YEAR)
    val timestamp: String get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

    fun isValid(): Boolean {
        return quantity > 0 && storeItem.isValid()
    }

    private fun hasValidQuantity(): Boolean {
        return quantity > 0
    }

    fun quantityToString(): String {
        return if (hasValidQuantity()) quantity.toString() else ""
    }

    override fun toString(): String {
        return "${"%.3f".format(weight).replace('.', ',')} kg, $storeItem"
    }

    fun emissionToString(): Spanned {
        return HtmlCompat.fromHtml("%.2f ".format(emission).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun weightToStringKg(): String {
        return "${"%.3f".format(weight)} kg"
    }

    fun weightToStringG(): String {
        return "${(weight * 1000).toInt()} g"
    }
}