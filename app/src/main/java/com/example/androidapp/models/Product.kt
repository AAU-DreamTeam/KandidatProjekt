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
              val ghCultivated: Boolean){

    constructor(): this(0, "", PRODUCT_CATEGORY.NONE, 0.0, 0.0, 0.0, 0.0, 0.0, false)

    fun isValid(): Boolean {
        return id > 0
    }
}