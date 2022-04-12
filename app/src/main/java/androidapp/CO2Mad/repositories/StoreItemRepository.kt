package androidapp.CO2Mad.repositories

import android.content.Context
import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.models.daos.StoreItemDao

class StoreItemRepository(context: Context){
    private val storeItemDao = StoreItemDao(context)

    fun loadAlternatives(storeItem: StoreItem): List<StoreItem> {
        return storeItemDao.loadAlternatives(storeItem)
    }

    fun close(){
        storeItemDao.close()
    }
}