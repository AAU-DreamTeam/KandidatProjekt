package com.example.androidapp.data.models

class Purchase(val id: Int,
               val storeItem: StoreItem,
               val timestamp: String,
               var quantity: Int){
    constructor(storeItem: StoreItem, timestamp: String, quantity: Int): this(0, storeItem, timestamp, quantity)

    var weight = storeItem.weight * quantity
    val emission = storeItem.emissionPerKg * weight

    fun isValid(): Boolean {
        return quantity > 0 && storeItem.isValid()
    }

    fun hasValidQuantity(): Boolean {
        return quantity > 0
    }

    override fun toString(): String {
        return "${"%.3f".format(weight)} kg, $storeItem"
    }

    fun weightToString(): String {
        return "${"%.3f".format(weight)} kg"
    }
}