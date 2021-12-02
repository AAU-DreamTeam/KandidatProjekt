package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.data.models.StoreItem
import com.example.androidapp.data.models.daos.StoreItemDao

class StoreItemRepository(context: Context){
    private val storeItemDao = StoreItemDao(context)

    fun loadAlternatives(storeItem: StoreItem): List<StoreItem> {
        return storeItemDao.loadAlternatives(storeItem)
    }

    fun loadAll(): List<StoreItem>{
        return storeItemDao.loadAll()
    }
}