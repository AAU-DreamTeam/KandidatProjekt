package com.example.androidapp.models

class Purchase(val id: Int,
               val storeItem: StoreItem,
               val timestamp: String,
               var quantity: Int){
    constructor(storeItem: StoreItem, timestamp: String, quantity: Int): this(0, storeItem, timestamp, quantity)

    var weight = storeItem.weight * quantity
    val emission: Double get() =  storeItem.emissionPerKg * weight

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