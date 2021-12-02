package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.daos.PurchaseDao
import com.google.mlkit.vision.text.Text

class PurchaseRepository(context: Context){
    private val purchaseDao = PurchaseDao(context)

    fun loadAllFromYearAndMonth(year: String, month: String): List<Purchase>{
        return purchaseDao.loadAllFromYearAndMonth(year, month)
    }

    fun loadAlternativeEmission(purchases: List<Purchase>): Double {
        return purchaseDao.loadAlternativeEmission(purchases)
    }

    fun generatePurchases(text: Text): MutableList<Purchase> {
        return purchaseDao.generatePurchases(text)
    }

    fun savePurchases(purchases: List<Purchase>): Boolean {
        return purchaseDao.savePurchases(purchases)
    }
}