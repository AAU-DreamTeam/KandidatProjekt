package com.example.androidapp.data

import android.content.Context
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.viewmodels.MONTH
import com.example.androidapp.views.MainActivity
import java.time.YearMonth

class PurchaseRepository(context: Context){
    private val dbManager = DBManager(context)

    fun getAllFromMonth(month: MONTH, year: String): List<Purchase>{
        return dbManager.fetchAllPurchasesFromMonth(month, year)
    }
}