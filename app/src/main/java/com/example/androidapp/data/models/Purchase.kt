package com.example.androidapp.data.models

import java.util.*

class Purchase(_storeItem: StoreItem, _quantity : Int){
    val storeItem = _storeItem
    val quantity = _quantity
    val emission = storeItem.emissionPerItem * quantity
}