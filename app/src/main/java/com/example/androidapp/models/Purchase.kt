package com.example.androidapp.models

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Month
import java.time.Year
import java.util.*

class Purchase(val id: Int,
               val storeItem: StoreItem,
               private val calendar: Calendar,
               var quantity: Int){
    constructor(storeItem: StoreItem, calendar: Calendar, quantity: Int): this(0, storeItem, calendar, quantity)

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

    fun weightToStringKg(): String {
        return "${"%.3f".format(weight)} kg"
    }

    fun weightToStringG(): String {
        return "${(weight * 1000).toInt()} g"
    }
}