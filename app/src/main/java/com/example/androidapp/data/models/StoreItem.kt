package com.example.androidapp.data.models

class StoreItem (val id: Int = 0,
                 val product: Product,
                 val country: Country,
                 val receiptText: String,
                 val organic: Boolean,
                 val packaged: Boolean,
                 val weight: Double,
                 val store: String){
    val emissionPerKg = calcEmission()
    val emissionPerItem = emissionPerKg * weight


    private fun calcEmission(): Double {
        val packagingEmission = if (packaged) product.packaging else 0.0

        var cultivationEmission = if (organic) 1.21 * product.cultivation else product.cultivation
        cultivationEmission = if (country.ghPenalty && product.ghCultivated) cultivationEmission * 7.5 else cultivationEmission

        return product.iluc + product.processing + product.retail + packagingEmission + cultivationEmission + country.transportEmission
    }

    override fun toString(): String {
        return "${product.name}, ${country.name}${if (organic) ", Øko" else ""}${if (!packaged) ", Løs" else ""}"
    }
}