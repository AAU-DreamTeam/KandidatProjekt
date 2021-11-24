package com.example.androidapp.data.models

import java.util.*

class Purchase(val id: Int = 0, val storeItem: StoreItem, val timestamp: String, val quantity: Int, val weight: Double = storeItem.weight * quantity){

    constructor(id: Int = 0, storeItem: StoreItem, timestamp: String, weight: Double): this(id, storeItem, timestamp, -1, weight)

    val emission : Double by lazy {storeItem.emissionPerKg * weight}

    override fun toString(): String {
        return "${"%.3f".format(weight * storeItem.weight)} kg, $storeItem"
    }
}