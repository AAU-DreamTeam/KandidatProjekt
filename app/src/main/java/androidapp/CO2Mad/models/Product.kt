package androidapp.CO2Mad.models

import androidapp.CO2Mad.tools.enums.ProductCategory

class Product(val id: Int,
              val name: String,
              val productCategory: ProductCategory,
              val cultivation: Double,
              val iluc: Double,
              val processing: Double,
              val packaging: Double,
              val retail: Double,
              val ghCultivated: Boolean,
              val countryId: Int,
              val weight : Double
              ){

    constructor(): this(0, "", ProductCategory.VEGETABLES, 0.0, 0.0, 0.0, 0.0, 0.0, false,0,0.0)

    fun isValid(): Boolean {
        return id > 0
    }

    override fun toString(): String {
        return name
    }

}
