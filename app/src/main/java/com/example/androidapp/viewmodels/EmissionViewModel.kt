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

    private val _weeklyEmission = MutableLiveData<Double>()
    val weeklyEmission: LiveData<Double> get() = _weeklyEmission

    private val _monthlyEmission = MutableLiveData<Double>()
    val monthlyEmission: LiveData<Double> get() = _monthlyEmission

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
        calcWeeklyEmission()
        calcMonthlyEmission()

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
    private fun calcWeeklyEmission(){
        var emissionSum  = 0.0
        val calender = Calendar.getInstance()
        calender.setTime(Calendar.getInstance().time)
        val weeknumber = calender.get(Calendar.WEEK_OF_YEAR)
        val year = calender.get(Calendar.YEAR)
        System.out.println("CurrentWeek"+weeknumber)
        System.out.println("CurrentYear"+year)
        System.out.println("CurrentMonth"+calender.get(Calendar.MONTH)+1)
        System.out.println("PruchasesSize "+purchases.value!!.size)

        purchases.value!!.forEach{

            System.out.println("WeekNumber"+it.weekNumber)
            System.out.println("Year"+it.year)
            System.out.println("Month"+it.month)
            if(it.year ==year && it.weekNumber == weeknumber){
                emissionSum += it.emission
            }else if(year > it.year && weeknumber > it.weekNumber){
                return@forEach
            }
        }

        _weeklyEmission.value = emissionSum
    }
    private fun calcMonthlyEmission(){
        var emissionSum  = 0.0
        val calender = Calendar.getInstance()
        calender.setTime(Calendar.getInstance().time)
        val month = calender.get(Calendar.MONTH)+1
        val year = calender.get(Calendar.YEAR)

        purchases.value!!.forEach{
            if(it.year == year && it.month == month) {
                emissionSum += it.emission
            }else if(year>it.year && month > it.month){
                return@forEach
            }
        }

        _monthlyEmission.value = emissionSum
    }

    override fun onCleared() {
        super.onCleared()
        purchaseRepository?.close()
        storeItemRepository?.close()
    }
}