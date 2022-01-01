package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.daos.PurchaseDao

class PurchaseRepository(context: Context){
    private val purchaseDao = PurchaseDao(context)

    fun loadPurchases(year: String, month: String): List<Purchase>{
        return purchaseDao.loadPurchases(year, month)
    }

    fun loadAlternativeEmission(purchases: List<Purchase>): Double {
        return purchaseDao.loadAlternativeEmission(purchases)
    }

    fun extractPurchases(imagePath: String, callback: (MutableList<Purchase>) -> Unit) {
        purchaseDao.extractPurchases(imagePath, callback)
    }

    fun savePurchases(purchases: List<Purchase>): Boolean {
        return purchaseDao.savePurchases(purchases)
    }

    fun close(){
        purchaseDao.close()
    }
}