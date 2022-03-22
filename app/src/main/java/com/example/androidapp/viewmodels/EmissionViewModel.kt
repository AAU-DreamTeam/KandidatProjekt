package com.example.androidapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidapp.models.enums.MONTH
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.StoreItem
import com.example.androidapp.repositories.PurchaseRepository
import com.example.androidapp.repositories.StoreItemRepository
import java.util.*

class EmissionViewModel: ViewModel()  {
    private var purchaseRepository: PurchaseRepository? = null
    private var storeItemRepository: StoreItemRepository? = null

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Copenhagen"))

    private val _year = MutableLiveData<String>()
    val year: LiveData<String> get() = _year

    private val _month = MutableLiveData<String>()
    val month: LiveData<String> get() = _month

    private val _purchases = MutableLiveData<List<Purchase>>()
    val purchases: LiveData<List<Purchase>> get() = _purchases

    private val _trips = MutableLiveData<MutableList<Pair<String, MutableList<Purchase>>>>(mutableListOf())
    val trips: LiveData<MutableList<Pair<String, MutableList<Purchase>>>> get() = _trips

    private val _totalEmission = MutableLiveData<Double>()
    val totalEmission: LiveData<Double> get() = _totalEmission

    private val _monthlyEmission = MutableLiveData<Double>()
    val monthlyEmission: LiveData<Double> get() = _monthlyEmission

    private val _weeklyEmission = MutableLiveData<Double>()
    val weeklyEmission: LiveData<Double> get() = _weeklyEmission

    private val _totalEmissionAlt = MutableLiveData<Double>()
    val totalEmissionAlt: LiveData<Double> get() = _totalEmissionAlt

    private val _emissionReduction = MutableLiveData<Double>()
    val emissionReduction: LiveData<Double> get() = _emissionReduction

    private val _emissionList = MutableLiveData<ArrayList<Double?>>()
    val emissionList: LiveData<ArrayList<Double?>> get() = _emissionList

    fun initiate(context: Context) {
        if (purchaseRepository == null) {
            purchaseRepository = PurchaseRepository(context)
        }

        if (storeItemRepository == null) {
            storeItemRepository = StoreItemRepository(context)
        }
    }

    fun loadData(){
        val monthTemp = calendar.get(Calendar.MONTH)

        _month.value = MONTH.values()[monthTemp].name
        _year.value = calendar.get(Calendar.YEAR).toString()
        _monthlyEmission.value = purchaseRepository!!.loadEmissionFromYearMonth(calendar)
        _weeklyEmission.value = purchaseRepository!!.loadEmissionFromYearWeek(calendar)
        _emissionList.value?.add(0,_weeklyEmission.value)
        _emissionList.value?.add(1, _weeklyEmission.value)

        _purchases.value = purchaseRepository!!.loadAllPurchases()
        //extractTrips()

        calcTotalEmission()
        _totalEmissionAlt.value = purchaseRepository!!.loadAlternativeEmission(_purchases.value!!)

        _emissionReduction.value = ((totalEmission.value!! - totalEmissionAlt.value!!)/totalEmission.value!!) * 100
    }

    /*fun extractTrips(){
        var prevTimestamp = ""
        var currTimestamp = ""
        var index = -1

        for (purchase in _purchases.value!!) {
            currTimestamp = purchase.timestamp

            if (prevTimestamp != currTimestamp) {
                val tripPurchases = mutableListOf<Purchase>()

                index++
                tripPurchases.add(purchase)
                _trips.value!!.add(index, Pair(currTimestamp, tripPurchases))
                prevTimestamp = purchase.timestamp
            } else {
                _trips.value!![index].second.add(purchase)
            }
        }
    }*/

    fun onViewAlternatives(purchaseId: Int): List<StoreItem> {
        return storeItemRepository!!.loadAlternatives(_purchases.value!![purchaseId].storeItem)
    }

    fun onViewPrev(){
        calendar.add(Calendar.MONTH, -1)

        loadData()
    }

    fun onViewNext(){
        calendar.add(Calendar.MONTH, 1)

        loadData()
    }

    private fun calcTotalEmission(){
        var emissionSum  = 0.0

        purchases.value!!.forEach{
            emissionSum += it.emission
        }

        _totalEmission.value = emissionSum
    }

    override fun onCleared() {
        super.onCleared()
        purchaseRepository?.close()
        storeItemRepository?.close()
    }
}