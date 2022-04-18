package androidapp.CO2Mad.viewmodels

import android.content.Context
import android.os.Build
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidapp.CO2Mad.models.enums.MONTH
import androidapp.CO2Mad.models.Purchase
import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.models.Trip
import androidapp.CO2Mad.repositories.PurchaseRepository
import androidapp.CO2Mad.repositories.StoreItemRepository
import java.util.*

class EmissionViewModel: ViewModel()  {
    private var purchaseRepository: PurchaseRepository? = null
    private var storeItemRepository: StoreItemRepository? = null
    private val emissionList = mutableListOf<Double>()
    private var position = 0
    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Copenhagen"))

    private val _emission = MutableLiveData<Double>(null)
    val emission: LiveData<Double> get() = _emission

    private val _purchases = MutableLiveData<List<Purchase>>()
    val purchases: LiveData<List<Purchase>> get() = _purchases

    private val _trips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> get() = _trips

    fun initiate(context: Context) {
        if (purchaseRepository == null) {
            purchaseRepository = PurchaseRepository(context)
        }

        if (storeItemRepository == null) {
            storeItemRepository = StoreItemRepository(context)
        }

        loadData()
    }

    fun loadData(){
        val (tripList, purchaseList) = purchaseRepository!!.loadAllTrips(3)

        _trips.value = tripList
        _purchases.value = purchaseList

        emissionList.clear()
        emissionList.add(purchaseRepository!!.loadEmissionFromYearWeek(calendar))
        emissionList.add(purchaseRepository!!.loadEmissionFromYearMonth(calendar))

        setEmission()
    }

    private fun setEmission() {
        _emission.value = emissionList[position]
    }

    fun onEmissionTimeRangeChanged(newPosition: Int) {
        position = newPosition
        setEmission()
    }

    override fun onCleared() {
        super.onCleared()
        purchaseRepository?.close()
        storeItemRepository?.close()
    }
}