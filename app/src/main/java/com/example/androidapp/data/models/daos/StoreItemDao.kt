package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.EmissionCalculator
import com.example.androidapp.data.models.Country
import com.example.androidapp.data.models.Product
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.StoreItem

class StoreItemDao(context: Context) {
    private val dbManager: DBManager = DBManager(context)

    fun getAlternatives(storeItem: StoreItem) : MutableList<StoreItem> {
        val query =
                "SELECT ${COLUMN_ID}, " +               // 0
                        "${COLUMN_RECEIPT_TEXT}, " +     // 1
                        "${COLUMN_ORGANIC}, " +         // 2
                        "${COLUMN_PACKAGED}, " +        // 3
                        "${COLUMN_WEIGHT}, " +          // 4
                        "${COLUMN_STORE}, " +           // 5
                        "${ProductDao.COLUMN_ID}, " +                // 6
                        "${ProductDao.COLUMN_NAME}, " +              // 7
                        "${ProductDao.COLUMN_CULTIVATION}, " +       // 8
                        "${ProductDao.COLUMN_ILUC}, " +              // 9
                        "${ProductDao.COLUMN_PROCESSING}, " +        // 10
                        "${ProductDao.COLUMN_PACKAGING}, " +         // 11
                        "${ProductDao.COLUMN_RETAIL}, " +            // 12
                        "${ProductDao.COLUMN_GHCULTIVATED}, " +      // 13
                        "${CountryDao.COLUMN_ID}, " +                // 14
                        "${CountryDao.COLUMN_NAME}, " +              // 15
                        "${CountryDao.COLUMN_TRANSPORT_EMISSION}, " + // 16
                        "${CountryDao.COLUMN_GHPENALTY}, " +          // 17
                        "MIN(${EmissionCalculator.sqlEmissionFormula()}) " +
                "FROM $TABLE " +
                "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON $COLUMN_COUNTRY_ID = ${CountryDao.COLUMN_ID} " +
                "WHERE $COLUMN_PRODUCT_ID = ${storeItem.product.id} " +
                "GROUP BY $COLUMN_ORGANIC, $COLUMN_PACKAGED;"

        return dbManager.selectMultiple(query) {
            produceStoreItem(it)
        }
    }

    companion object{
        val TABLE = "storeItem"
        val COLUMN_COUNT = 6
        val COLUMN_COUNTRY_ID = "$TABLE.countryID"
        val COLUMN_PRODUCT_ID = "$TABLE.productID"

        val COLUMN_ID = "$TABLE.id"
        val COLUMN_ID_POSITION = 0

        val COLUMN_RECEIPT_TEXT = "$TABLE.receiptText"
        val COLUMN_RECEIPT_TEXT_POSITION = 1

        val COLUMN_ORGANIC = "$TABLE.organic"
        val COLUMN_ORGANIC_POSITION = 2

        val COLUMN_PACKAGED = "$TABLE.packaged"
        val COLUMN_PACKAGED_POSITION = 3

        val COLUMN_WEIGHT = "$TABLE.weight"
        val COLUMN_WEIGHT_POSITION = 4

        val COLUMN_STORE = "$TABLE.store"
        val COLUMN_STORE_POSITION = 5

        fun produceStoreItem(cursor: Cursor, startIndex: Int = 0): StoreItem {
            val product = ProductDao.produceProduct(cursor, startIndex + COLUMN_COUNT)
            val country = CountryDao.produceCountry(cursor, startIndex + COLUMN_COUNT + ProductDao.COLUMN_COUNT)

            return StoreItem(
                    cursor.getInt(startIndex + COLUMN_ID_POSITION),
                    product,
                    country,
                    cursor.getString(startIndex + COLUMN_RECEIPT_TEXT_POSITION),
                    cursor.getInt(startIndex + COLUMN_ORGANIC_POSITION) != 0,
                    cursor.getInt(startIndex + COLUMN_PACKAGED_POSITION) != 0,
                    cursor.getDouble(startIndex + COLUMN_WEIGHT_POSITION),
                    cursor.getString(startIndex + COLUMN_STORE_POSITION)
            )
        }
    }
}