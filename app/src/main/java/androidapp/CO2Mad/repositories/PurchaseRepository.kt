package androidapp.CO2Mad.repositories

import android.content.Context
import androidapp.CO2Mad.models.Purchase
import androidapp.CO2Mad.models.Trip
import androidapp.CO2Mad.models.daos.PurchaseDao
import java.util.*

class PurchaseRepository(context: Context){
    private val purchaseDao = PurchaseDao(context)

    fun loadEmissionFromYearMonth(calendar: Calendar): Double {
        return purchaseDao.loadEmissionFromYearMonth(calendar)
    }

    fun loadEmissionFromYearWeek(calendar: Calendar): Double {
        return purchaseDao.loadEmissionFromYearWeek(calendar)
    }

    fun loadAllTrips(numberOfAlternatives: Int): Pair<MutableList<Trip>, MutableList<Purchase>> {
        return purchaseDao.loadAllTrips(numberOfAlternatives)
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

    fun deletePurchase(id: Int) {
        purchaseDao.deletePurchase(id)
    }
}