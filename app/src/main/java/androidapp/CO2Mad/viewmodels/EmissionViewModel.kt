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

        val (tripList, purchaseList) = purchaseRepository!!.loadAllTrips()

        _trips.value = tripList
        _purchases.value = purchaseList
    }

    override fun onCleared() {
        super.onCleared()
        purchaseRepository?.close()
        storeItemRepository?.close()
    }
}