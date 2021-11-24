package com.example.androidapp.data.models

class StoreItem (_product: Product, _country: Country, _organic: Boolean, _packaged: Boolean, _weight: Double){
    val product = _product
    val country = _country
    val organic = _organic
    val packaged = _packaged
    val weight = _weight
    val emissionPerKg = calcEmission()
    val emissionPerItem = emissionPerKg * weight


    private fun calcEmission(): Double {
        val packagingEmission = if (packaged) product.packaging else 0.0

        var cultivationEmission = if (organic) 1.21 * product.cultivation else product.cultivation
        cultivationEmission = if (country.ghPenalty) cultivationEmission * 7.5 else cultivationEmission

        return product.iluc + product.processing + product.retail + packagingEmission + cultivationEmission + country.transportEmission
    }

    override fun toString(): String {
        return "${product.name}, ${country.name}${if (organic) ", Øko" else ""}${if (!packaged) ", Løs" else ""}"
    }
}