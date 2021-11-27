package com.example.androidapp.data.models

class Purchase(val id: Int,
               val storeItem: StoreItem,
               val timestamp: String,
               var weight: Double){

    constructor(storeItem: StoreItem, timestamp: String, quantity: Int): this(0, storeItem, timestamp, storeItem.weight * quantity)

    val emission : Double by lazy {storeItem.emissionPerKg * weight}

    override fun toString(): String {
        return "${"%.3f".format(weight)} kg, $storeItem"
    }

    fun weightToString(): String {
        return "${"%.3f".format(weight)} kg"
    }
}