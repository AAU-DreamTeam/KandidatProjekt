package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Country
import org.w3c.dom.Text

class CountryDao(private val dbManager: DBManager) {
    constructor(context: Context): this(DBManager(context))

    fun loadCountries(): List<String> {
        val query = "SELECT $COLUMN_NAME FROM $TABLE;"

        return dbManager.selectMultiple(query){
            it.getString(0)
        }
    }

    fun extractCountry(receiptText: String): Country{
        var result = Country()
        val lowerReceiptText = receiptText.toLowerCase()
        val query =
                "SELECT $COLUMN_ID, " +                   // 14
                    "$COLUMN_NAME, " +                 // 15
                    "$COLUMN_TRANSPORT_EMISSION, " +   // 16
                    "$COLUMN_GHPENALTY " +            // 17
                "FROM $TABLE " +
                "WHERE '$lowerReceiptText' LIKE '%'||LOWER($COLUMN_NAME)||'%';"

        dbManager.select(query) {
            result = produceCountry(it)
        }

        return result
    }

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