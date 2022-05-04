package androidapp.CO2Mad.models

import android.text.Spanned
import androidapp.CO2Mad.tools.enums.ProductCategory
import androidapp.CO2Mad.tools.enums.Rating
import androidx.core.text.HtmlCompat
import androidapp.CO2Mad.tools.EmissionCalculator

class StoreItem (val id: Int,
                 var product: Product,
                 var country: Country,
                 var receiptText: String,
                 var organic: Boolean,
                 var packaged: Boolean,
                 var weight: Double,
                 var countryDefault: Boolean,
                 var weightDefault: Boolean,
                 val store: String = "Føtex"){

    constructor(product: Product,
                country: Country,
                _receiptText: String,
                organic: Boolean,
                packaged: Boolean,
                weight: Double,
                countryDefault: Boolean,
                weightDefault: Boolean,
                store: String = "Føtex"
                ): this(0, product, country, _receiptText, organic, packaged, weight,countryDefault,weightDefault,store)

    val emissionPerKg: Double get() = EmissionCalculator.calcEmission(this)
    var altEmissions: List<Pair<Int, Double>>? = null
    var rating: Rating? = Rating.VERY_GOOD

    private fun hasValidWeight(): Boolean {
        return weight > 0.0
    }
    fun isValid(): Boolean{
        return receiptText.isNotEmpty() && hasValidWeight() && product.isValid() && country.validate()
    }

    fun rate(original: StoreItem? = null) {
        rating = if (original == null || product.productCategory != ProductCategory.VEGETABLES) {
            EmissionCalculator.rate(this)
        } else {
            EmissionCalculator.rateAlternative(original, this)
        }
    }

    fun weightToString(inGrams: Boolean = false): String {
        return if (hasValidWeight()) (if (inGrams) (weight * 1000).toInt().toString() else weight.toString()) else ""
    }

    fun emissionToString(): Spanned{
        return HtmlCompat.fromHtml("%.2f ".format(emissionPerKg).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub> pr. kg", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun altEmissionDifferenceText(): Spanned {
        return HtmlCompat.fromHtml("%.2f ".format(((emissionPerKg - altEmissions!!.first().second) / emissionPerKg) * 100).replace('.', ','), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun differenceText(compare: StoreItem): Spanned {
        return HtmlCompat.fromHtml("%.2f ".format(((compare.emissionPerKg - emissionPerKg) / compare.emissionPerKg) * 100).replace('.', ','), HtmlCompat.FROM_HTML_MODE_LEGACY)
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
