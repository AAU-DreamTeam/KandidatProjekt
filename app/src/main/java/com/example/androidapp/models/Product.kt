package com.example.androidapp.models

import com.example.androidapp.models.enums.PRODUCT_CATEGORY

class Product(val id: Int,
              val name: String,
              val productCategory: PRODUCT_CATEGORY,
              val cultivation: Double,
              val iluc: Double,
              val processing: Double,
              val packaging: Double,
              val retail: Double,
              val ghCultivated: Boolean,
              val countryId: Int,
              val weight : Double
              ){

    constructor(): this(0, "", PRODUCT_CATEGORY.VEGETABLES, 0.0, 0.0, 0.0, 0.0, 0.0, false,0,0.0)

    fun isValid(): Boolean {
        return id > 0
    }
}
