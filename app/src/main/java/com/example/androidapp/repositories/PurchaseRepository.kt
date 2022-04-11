package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.Trip
import com.example.androidapp.models.daos.CountryDao
import com.example.androidapp.models.daos.ProductDao
import com.example.androidapp.models.daos.PurchaseDao
import com.example.androidapp.models.daos.StoreItemDao
import com.example.androidapp.models.tools.EmissionCalculator
import java.text.SimpleDateFormat
import java.util.*

class PurchaseRepository(context: Context){
    private val purchaseDao = PurchaseDao(context)

    fun loadEmissionFromYearMonth(calendar: Calendar): Double {
        return purchaseDao.loadEmissionFromYearMonth(calendar)
    }

    fun loadEmissionFromYearWeek(calendar: Calendar): Double {
        return purchaseDao.loadEmissionFromYearWeek(calendar)
    }

    fun loadAllTrips(): Pair<List<Trip>, List<Purchase>> {
        return purchaseDao.loadAllTrips()
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