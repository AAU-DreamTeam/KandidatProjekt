package com.example.androidapp.models

import java.util.*

class Trip(val purchases: MutableList<Purchase>) {
    fun prettyTimestamp(): String{
        return purchases[0].timestamp
    }
}