package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Country
import org.w3c.dom.Text

class CountryDao(private val dbManager: DBManager) {
    constructor(context: Context): this(DBManager(context))

    fun loadCountries(): List<Country> {
        val query = "SELECT * FROM $TABLE;"

        return dbManager.selectMultiple(query){
            produceCountry(it)
        }
    }

    fun extractCountry(receiptText: String): Country{
        var result = Country()
        val query =
                "SELECT * " +
                "FROM $TABLE " +
                "WHERE '$receiptText' LIKE '%'||$COLUMN_NAME||'%';"

        dbManager.select(query) {
            result = produceCountry(it)
        }

        return result
    }

    companion object {
        const val TABLE = "country"
        private const val COLUMN_COUNT = 4

        const val COLUMN_ID = "id"
        private const val COLUMN_ID_POSITION = 0

        const val COLUMN_NAME = "name"
        private const val COLUMN_NAME_POSITION = 1

        const val COLUMN_TRANSPORT_EMISSION = "transportEmission"
        private const val COLUMN_TRANSPORT_EMISSION_POSITION = 2

        const val COLUMN_GHPENALTY = "GHPenalty"
        private const val COLUMN_GHPENALTY_POSITION = 3

        const val ALL_COLUMNS =
                "$TABLE.$COLUMN_ID, " +                   // 14
                "$TABLE.$COLUMN_NAME, " +                 // 15
                "$TABLE.$COLUMN_TRANSPORT_EMISSION, " +   // 16
                "$TABLE.$COLUMN_GHPENALTY"

        fun produceCountry(cursor: Cursor, startIndex: Int = 0): Country {
            return Country(
                    cursor.getInt(startIndex + COLUMN_ID_POSITION),
                    cursor.getString(startIndex + COLUMN_NAME_POSITION),
                    cursor.getDouble(startIndex + COLUMN_TRANSPORT_EMISSION_POSITION),
                    cursor.getInt(startIndex + COLUMN_GHPENALTY_POSITION) != 0
            )
        }
    }
}