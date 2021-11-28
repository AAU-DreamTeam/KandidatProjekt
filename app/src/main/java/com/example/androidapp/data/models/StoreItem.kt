package com.example.androidapp.data.models

import com.example.androidapp.data.EmissionCalculator

class StoreItem (val id: Int,
                 val product: Product,
                 val country: Country,
                 val receiptText: String,
                 val organic: Boolean,
                 val packaged: Boolean,
                 val weight: Double,
                 val store: String = "Føtex"){

    constructor(product: Product,
                country: Country,
                receiptText: String,
                organic: Boolean,
                packaged: Boolean,
                weight: Double,
                store: String = "Føtex"): this(0, product, country, receiptText, organic, packaged, weight, store)
    val emissionPerKg = EmissionCalculator.calcEmission(this)
    val emissionPerItem = weight * emissionPerKg

    override fun toString(): String {
        return "${product.name}, ${country.name}${if (organic) ", Øko" else ""}${if (!packaged) ", Løs" else ""}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoreItem

        if (product.id != other.product.id) return false
        if (country.id != other.country.id) return false
        if (organic != other.organic) return false
        if (packaged != other.packaged) return false

        return true
    }

    override fun hashCode(): Int {
        var result = product.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + organic.hashCode()
        result = 31 * result + packaged.hashCode()
        return result
    }
}