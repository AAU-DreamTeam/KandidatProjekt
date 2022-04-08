package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.models.StoreItem
import com.example.androidapp.models.daos.StoreItemDao

class StoreItemRepository(context: Context){
    private val storeItemDao = StoreItemDao(context)

    fun loadAlternatives(storeItem: StoreItem,  numberOfAlternatives: Int): List<StoreItem> {
        return storeItemDao.loadAlternatives(storeItem, numberOfAlternatives)
    }

    fun loadStoreItems(): List<StoreItem>{
        return storeItemDao.loadStoreItems()
    }

    fun close(){
        storeItemDao.close()
    }
}