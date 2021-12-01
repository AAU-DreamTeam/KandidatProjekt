package com.example.androidapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidapp.data.MONTH
import com.example.androidapp.repositories.PurchaseRepository
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.StoreItem
import com.example.androidapp.repositories.StoreItemRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EmissionViewModel: ViewModel()  {
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

    fun loadData(context: Context){
        val monthTemp = calendar.get(Calendar.MONTH)

        _month.value = MONTH.values()[monthTemp].name
        _year.value = currentYear()

        _purchases.value = PurchaseRepository(context).loadAllFromYearAndMonth(year.value!!, String.format("%02d", monthTemp + 1))

        calcTotalEmission()
        calcTotalEmissionAlt(PurchaseRepository(context).loadAlternativeEmissions(_purchases.value!!))

        _emissionReduction.value = ((totalEmission.value!! - totalEmissionAlt.value!!)/totalEmission.value!!) * 100
    }

    fun loadAlternatives(context: Context, purchaseId: Int): List<StoreItem> {
        return StoreItemRepository(context).loadAlternatives(_purchases.value!![purchaseId].storeItem)
    }

    fun loadPrev(context: Context){
        calendar.add(Calendar.MONTH, -1)

        loadData(context)
    }

    fun loadNext(context: Context){
        calendar.add(Calendar.MONTH, 1)

        loadData(context)
    }

    private fun calcTotalEmissionAlt(emissions: List<Double>){
        var emissionSum  = 0.0

        emissions.forEach{
            emissionSum += it
        }

        _totalEmissionAlt.value = emissionSum
    }

    private fun calcTotalEmission(){
        var emissionSum  = 0.0

        purchases.value!!.forEach{
            emissionSum += it.emission
        }

        _totalEmission.value = emissionSum
    }

    private fun currentMonth() : Int{
        val dateFormat: DateFormat = SimpleDateFormat("MM")
        val date = Date()

        return dateFormat.format(date).toInt() - 1
    }

    private fun currentYear() : String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy")
        val date = Date()

        return dateFormat.format(date)
    }
}