package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.daos.PurchaseDao
import com.example.androidapp.viewmodels.MONTH

class PurchaseRepository(context: Context){
    private val purchaseDao = PurchaseDao(context)

    fun loadAllFromYearAndMonth(year: String, month: MONTH): List<Purchase>{
        return purchaseDao.loadAllFromYearAndMonth(year, month)
    }

    fun loadAlternativeEmissions(purchases: List<Purchase>): List<Double> {
        return purchaseDao.getAlternativeEmissions(purchases)
    }
}