package com.example.androidapp.data.models

import com.example.androidapp.data.EmissionCalculator

class StoreItem (val id: Int = 0,
                 val product: Product,
                 val country: Country,
                 val receiptText: String,
                 val organic: Boolean,
                 val packaged: Boolean,
                 val weight: Double,
                 val store: String){

    val emissionPerKg by lazy { EmissionCalculator.calcEmission(this) }

    override fun toString(): String {
        return "${product.name}, ${country.name}${if (organic) ", Øko" else ""}${if (!packaged) ", Løs" else ""}"
    }
}