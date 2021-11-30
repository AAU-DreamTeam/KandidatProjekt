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

    fun loadAlternativeEmissions(purchases: List<Purchase>): List<Double> {
        return purchaseDao.loadAlternativeEmissions(purchases)
    }

    fun generatePurchases(text: Text): MutableList<Purchase> {
        return purchaseDao.generatePurchases(text)
    }

    fun savePurchases(purchases: List<Purchase>) {
        purchaseDao.savePurchases(purchases)
    }
}