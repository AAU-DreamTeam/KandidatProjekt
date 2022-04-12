package androidapp.CO2Mad.repositories

import android.content.Context
import androidapp.CO2Mad.models.Purchase
import androidapp.CO2Mad.models.Trip
import androidapp.CO2Mad.models.daos.CountryDao
import androidapp.CO2Mad.models.daos.ProductDao
import androidapp.CO2Mad.models.daos.PurchaseDao
import androidapp.CO2Mad.models.daos.StoreItemDao
import androidapp.CO2Mad.models.tools.EmissionCalculator
import java.text.SimpleDateFormat
import java.util.*

class PurchaseRepository(context: Context){
    private val purchaseDao = PurchaseDao(context)

    fun loadEmissionFromYearMonth(calendar: Calendar): Double {
        return purchaseDao.loadEmissionFromYearMonth(calendar)
    }

    fun loadEmissionFromYearWeek(calendar: Calendar): Double {
        return purchaseDao.loadEmissionFromYearWeek(calendar)
    }

    fun loadAllTrips(): Pair<List<Trip>, List<Purchase>> {
        return purchaseDao.loadAllTrips()
    }

    fun extractPurchases(imagePath: String, callback: (MutableList<Purchase>) -> Unit) {
        purchaseDao.extractPurchases(imagePath, callback)
    }

    fun savePurchases(purchases: List<Purchase>): Boolean {
        return purchaseDao.savePurchases(purchases)
    }

    fun close(){
        purchaseDao.close()
    }
}