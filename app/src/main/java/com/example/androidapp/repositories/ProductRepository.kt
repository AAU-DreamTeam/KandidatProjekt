package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.data.models.Product
import com.example.androidapp.data.models.daos.CountryDao
import com.example.androidapp.data.models.daos.ProductDao

class ProductRepository(context: Context) {
    private val productDao = ProductDao(context)

    fun loadProducts(): List<Product> {
        return productDao.loadProducts()
    }
}