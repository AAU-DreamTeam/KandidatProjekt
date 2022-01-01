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

    private val timeZone = "Europe/Copenhagen"
    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone))

    private val _year = MutableLiveData<String>()
    val year: LiveData<String> get() = _year

    private val _month = MutableLiveData<String>()
    val month: LiveData<String> get() = _month

    private val _purchases = MutableLiveData<List<Purchase>>()
    val purchases: LiveData<List<Purchase>> get() = _purchases

    private val _totalEmission = MutableLiveData<Double>()
    val totalEmission: LiveData<Double> get() = _totalEmission

    private val _totalEmissionAlt = MutableLiveData<Double>()
    val totalEmissionAlt: LiveData<Double> get() = _totalEmissionAlt

    private val _emissionReduction = MutableLiveData<Double>()
    val emissionReduction: LiveData<Double> get() = _emissionReduction

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

        _purchases.value = purchaseRepository!!.loadPurchases(year.value!!, String.format("%02d", monthTemp + 1))

        calcTotalEmission()
        _totalEmissionAlt.value = purchaseRepository!!.loadAlternativeEmission(_purchases.value!!)

        _emissionReduction.value = ((totalEmission.value!! - totalEmissionAlt.value!!)/totalEmission.value!!) * 100
    }

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