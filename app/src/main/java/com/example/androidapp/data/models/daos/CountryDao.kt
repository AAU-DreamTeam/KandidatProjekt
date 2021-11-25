package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Country

class CountryDao(context: Context) {
    private val dbManager: DBManager = DBManager(context)

    companion object {
        fun produceCountry(cursor: Cursor, startIndex: Int = 0): Country {
            return Country(
                    cursor.getInt(startIndex),
                    cursor.getString(startIndex + 1),
                    cursor.getDouble(startIndex + 2),
                    cursor.getInt(startIndex + 3) != 0
            )
        }
    }
}