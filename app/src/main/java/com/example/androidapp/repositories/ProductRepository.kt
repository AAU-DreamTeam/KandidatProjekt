package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.models.Product
import com.example.androidapp.models.daos.ProductDao

class ProductRepository(context: Context) {
    private val productDao = ProductDao(context)

    fun loadProducts(): List<Product> {
        return productDao.loadProducts()
    }

    fun close(){
        productDao.close()
    }
}