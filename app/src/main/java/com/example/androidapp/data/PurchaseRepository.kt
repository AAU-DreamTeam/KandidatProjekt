package com.example.androidapp.data

import android.content.Context
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.daos.PurchaseDao
import com.example.androidapp.viewmodels.MONTH
import com.example.androidapp.views.MainActivity
import java.time.YearMonth

class PurchaseRepository(context: Context){
    private val purchaseDao = PurchaseDao(context)

    fun getAllFromMonth(year: String, month: MONTH): List<Purchase>{
        return purchaseDao.getAllFromYearAndMonth(year, month)
    }
}