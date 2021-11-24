package com.example.androidapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidapp.data.PurchaseRepository
import com.example.androidapp.data.models.Purchase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EmissionViewModel: ViewModel()  {
    private val _year = MutableLiveData<String>()
    val year: LiveData<String> get() = _year

    private val _month = MutableLiveData<MONTH>()
    val month: LiveData<MONTH> get() = _month

    private val _purchaseList = MutableLiveData<List<Purchase>>()
    val purchaseList: LiveData<List<Purchase>> get() = _purchaseList

    private val _totalEmission = MutableLiveData<Double>()
    val totalEmission: LiveData<Double> get() = _totalEmission

    private val _totalEmissionAlt = MutableLiveData<Double>()
    val totalEmissionAlt: LiveData<Double> get() = _totalEmissionAlt

    private val _emissionReduction = MutableLiveData<Int>()
    val emissionReduction: LiveData<Int> get() = _emissionReduction

    fun onStartUp(context: Context){
        _month.value = MONTH.values()[currentMonth()]
        _year.value = currentYear()

        _purchaseList.value = PurchaseRepository(context).getAllFromMonth(month.value!!, year.value!!)

        var emissionSum  = 0.0

        purchaseList.value!!.forEach{
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