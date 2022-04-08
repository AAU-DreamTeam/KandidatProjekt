package com.example.androidapp.viewmodels

import android.content.Context
import android.os.Build
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidapp.models.enums.MONTH
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.StoreItem
import com.example.androidapp.models.Trip
import com.example.androidapp.repositories.PurchaseRepository
import com.example.androidapp.repositories.StoreItemRepository
import java.util.*

class EmissionViewModel: ViewModel()  {
    private var purchaseRepository: PurchaseRepository? = null
    private var storeItemRepository: StoreItemRepository? = null

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Copenhagen"))

    private val _purchases = MutableLiveData<List<Purchase>>()
    val purchases: LiveData<List<Purchase>> get() = _purchases

    private val _trips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> get() = _trips

    private val _monthlyEmission = MutableLiveData<Double>()
    val monthlyEmission: LiveData<Double> get() = _monthlyEmission

    private val _weeklyEmission = MutableLiveData<Double>()
    val weeklyEmission: LiveData<Double> get() = _weeklyEmission

    private val _emissionList = MutableLiveData<MutableList<Double?>>()
    val emissionList: LiveData<MutableList<Double?>> get() = _emissionList

    fun initiate(context: Context) {
        if (purchaseRepository == null) {
            purchaseRepository = PurchaseRepository(context)
        }

        if (storeItemRepository == null) {
            storeItemRepository = StoreItemRepository(context)
        }
    }

    fun loadData(){
        _monthlyEmission.value = purchaseRepository!!.loadEmissionFromYearMonth(calendar)
        _weeklyEmission.value = purchaseRepository!!.loadEmissionFromYearWeek(calendar)

        _emissionList.value = mutableListOf(_weeklyEmission.value,_monthlyEmission.value)

        _purchases.value = purchaseRepository!!.loadAllPurchases()
        _trips.value = purchaseRepository!!.loadAllTrips()
    }

    override fun onCleared() {
        super.onCleared()
        purchaseRepository?.close()
        storeItemRepository?.close()
    }
}