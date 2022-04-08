package com.example.androidapp.models

import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.example.androidapp.models.tools.EmissionCalculator
import java.util.*

class StoreItem (val id: Int,
                 var product: Product,
                 var country: Country,
                 _receiptText: String,
                 var organic: Boolean,
                 var packaged: Boolean,
                 var weight: Double,
                 val store: String = "Føtex"){

    constructor(product: Product,
                country: Country,
                _receiptText: String,
                organic: Boolean,
                packaged: Boolean,
                weight: Double,
                store: String = "Føtex"): this(0, product, country, _receiptText, organic, packaged, weight, store)

    var weightDefault = false
    var receiptText = _receiptText
    val emissionPerKg: Double get() = EmissionCalculator.calcEmission(this)
    var altEmission: Pair<Int, Double> = Pair(0, emissionPerKg)

    fun hasValidWeight(): Boolean {
        return weight > 0.0
    }
    fun isValid(): Boolean{
        return receiptText.isNotEmpty() && hasValidWeight() && product.isValid() && country.validate()
    }

    fun weightToString(inGrams: Boolean = false): String {
        return if (hasValidWeight()) (if (inGrams) (weight * 1000).toInt().toString() else weight.toString()) else ""
    }

    fun emissionToString(): Spanned{
        return HtmlCompat.fromHtml("%.2f ".format(emissionPerKg).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub> pr. kg", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun altEmissionDifference(): Spanned {
        return HtmlCompat.fromHtml("%.2f ".format(((emissionPerKg - altEmission.second) / emissionPerKg) * 100).replace('.', ','), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun difference(compare: StoreItem): Spanned {
        return HtmlCompat.fromHtml("%.2f ".format((((compare.emissionPerKg - emissionPerKg) / compare.emissionPerKg) * 100)).replace('.', ','), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

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