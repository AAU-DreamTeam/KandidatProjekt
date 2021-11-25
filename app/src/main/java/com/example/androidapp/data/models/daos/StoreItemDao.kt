package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Country
import com.example.androidapp.data.models.Product
import com.example.androidapp.data.models.StoreItem

class StoreItemDao(context: Context) {
    private val dbManager: DBManager = DBManager(context)

    fun getAlternatives(storeItem: StoreItem) : MutableList<StoreItem> {
        val query =
                "SELECT storeItem.id, " +               // 0
                        "storeItem.receiptText, " +     // 1
                        "storeItem.organic, " +         // 2
                        "storeItem.packaged, " +        // 3
                        "storeItem.weight, " +          // 4
                        "storeItem.store, " +           // 5
                        "product.id, " +                // 6
                        "product.name, " +              // 7
                        "product.cultivation, " +       // 8
                        "product.iluc, " +              // 9
                        "product.processing, " +        // 10
                        "product.packaging, " +         // 11
                        "product.retail, " +            // 12
                        "product.GHCultivated, " +      // 13
                        "country.id, " +                // 14
                        "country.name, " +              // 15
                        "country.transportEmission, " + // 16
                        "country.GHPenalty " +          // 17
                        "FROM storeItem " +
                        "INNER JOIN product ON storeItem.productID = product.id " +
                        "INNER JOIN storeItem.countryID ON country.id;"

        return dbManager.selectMultiple(query) {
            produceStoreItem(it)
        }
    }

    companion object{
        fun produceStoreItem(cursor: Cursor, startIndex: Int = 0): StoreItem {

            val product = ProductDao.produceProduct(cursor, startIndex + 6)
            val country = CountryDao.produceCountry(cursor, startIndex + 14)

            return StoreItem(
                    cursor.getInt(startIndex),
                    product,
                    country,
                    cursor.getString(startIndex + 1),
                    cursor.getInt(startIndex + 2) != 0,
                    cursor.getInt(startIndex + 3) != 0,
                    cursor.getDouble(startIndex + 4),
                    cursor.getString(startIndex + 5)
            )
    }

    }
}