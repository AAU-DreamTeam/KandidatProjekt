package androidapp.CO2Mad.repositories

import android.content.Context
import androidapp.CO2Mad.models.Product
import androidapp.CO2Mad.models.daos.ProductDao

class ProductRepository(context: Context) {
    private val productDao = ProductDao(context)

    fun loadProducts(): List<Product> {
        return productDao.loadProducts()
    }

    fun close(){
        productDao.close()
    }
}