package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.EmissionCalculator
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.viewmodels.MONTH
import kotlin.text.StringBuilder

class PurchaseDao(context: Context) {
    private val dbManager = DBManager(context)

    fun loadAllFromYearAndMonth(year: String, month: MONTH): MutableList<Purchase> {
        val query =
                "SELECT $COLUMN_ID, " +                //0
                       "$COLUMN_TIMESTAMP, " +         //1
                       "SUM($COLUMN_WEIGHT), " +       //2
                       "${StoreItemDao.COLUMN_ID}, " +               //3
                       "${StoreItemDao.COLUMN_RECEIPT_TEXT}, " +      //4
                       "${StoreItemDao.COLUMN_ORGANIC}, " +          //5
                       "${StoreItemDao.COLUMN_PACKAGED}, " +         //6
                       "${StoreItemDao.COLUMN_WEIGHT}, " +           //7
                       "${StoreItemDao.COLUMN_STORE}, " +            //8
                       "${ProductDao.COLUMN_ID}, " +                 //9
                       "${ProductDao.COLUMN_NAME}, " +               //10
                       "${ProductDao.COLUMN_CULTIVATION}, " +        //11
                       "${ProductDao.COLUMN_ILUC}, " +               //12
                       "${ProductDao.COLUMN_PROCESSING}, " +         //13
                       "${ProductDao.COLUMN_PACKAGING}, " +          //14
                       "${ProductDao.COLUMN_RETAIL}, " +             //15
                       "${ProductDao.COLUMN_GHCULTIVATED}, " +       //16
                       "${CountryDao.COLUMN_ID}, " +                 //17
                       "${CountryDao.COLUMN_NAME}, " +               //18
                       "${CountryDao.COLUMN_TRANSPORT_EMISSION}, " +  //19
                       "${CountryDao.COLUMN_GHPENALTY} " +           //20
                "FROM $TABLE " +
                "INNER JOIN ${StoreItemDao.TABLE} ON $COLUMN_STORE_ITEM_ID = ${StoreItemDao.COLUMN_ID} " +
                "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.COLUMN_ID} " +
                "WHERE strftime('%Y', $COLUMN_TIMESTAMP) = '$year' AND strftime('%m', $COLUMN_TIMESTAMP) = '${month.position}' " +
                "GROUP BY ${ProductDao.COLUMN_ID}, ${CountryDao.COLUMN_ID}, ${StoreItemDao.COLUMN_ORGANIC}, ${StoreItemDao.COLUMN_PACKAGED};"

        return dbManager.selectMultiple(query){
            producePurchase(it)
        }
    }

    fun getAlternativeEmissions(purchases: List<Purchase>): List<Double> {
        val productIds = StringBuilder("(")
        var first = true

        purchases.forEach(){
            if (!first) {
                productIds.append(", ")
            } else {
                first = false
            }

            productIds.append("${it.storeItem.product.id}")
        }

        productIds.append(")")

        val query =
                "SELECT MIN(${COLUMN_WEIGHT} * ${EmissionCalculator.sqlEmissionFormula()})" +
                        "FROM $TABLE " +
                        "INNER JOIN ${StoreItemDao.TABLE} ON $COLUMN_STORE_ITEM_ID = ${StoreItemDao.COLUMN_ID} " +
                        "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.COLUMN_ID} " +
                        "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.COLUMN_ID} " +
                        "WHERE ${ProductDao.COLUMN_ID} IN $productIds" +
                        "GROUP BY ${COLUMN_ID};"

        return dbManager.selectMultiple(query) {
            it.getDouble(0)
        }
    }

    companion object {
        val TABLE = "purchase"
        val COLUMN_COUNT = 3 // Excluding foreign keys
        val COLUMN_STORE_ITEM_ID = "$TABLE.storeItemID"

        val COLUMN_ID = "$TABLE.id"
        val COLUMN_ID_POSITION = 0

        val COLUMN_TIMESTAMP = "$TABLE.timestamp"
        val COLUMN_TIMESTAMP_POSITION = 1

        val COLUMN_WEIGHT = "$TABLE.weight"
        val COLUMN_WEIGHT_POSITION = 2

        fun producePurchase(cursor: Cursor, startIndex: Int = 0): Purchase{
            val storeItem = StoreItemDao.produceStoreItem(cursor, startIndex + COLUMN_COUNT)

            return Purchase(cursor.getInt(startIndex + COLUMN_ID_POSITION), storeItem, cursor.getString(startIndex + COLUMN_TIMESTAMP_POSITION), cursor.getDouble(startIndex + COLUMN_WEIGHT_POSITION))
        }
    }
}