package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Country

class CountryDao(context: Context) {
    private val dbManager: DBManager = DBManager(context)

    companion object {
        val TABLE = "country"
        val COLUMN_COUNT = 4

        val COLUMN_ID = "$TABLE.id"
        val COLUMN_ID_POSITION = 0

        val COLUMN_NAME = "$TABLE.name"
        val COLUMN_NAME_POSITION = 1

        val COLUMN_TRANSPORT_EMISSION = "$TABLE.transportEmission"
        val COLUMN_TRANSPORT_EMISSION_POSITION = 2

        val COLUMN_GHPENALTY = "$TABLE.GHPenalty"
        val COLUMN_GHPENALTY_POSITION = 3

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