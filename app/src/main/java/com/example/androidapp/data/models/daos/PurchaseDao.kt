package com.example.androidapp.data.models.daos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.EmissionCalculator
import com.example.androidapp.data.models.Purchase
import com.google.mlkit.vision.text.Text
import java.text.SimpleDateFormat
import java.util.*

class PurchaseDao(context: Context) {
    private val dbManager = DBManager.getInstance(context)

    fun loadAllFromYearAndMonth(year: String, month: String): List<Purchase> {
        val results = mutableListOf<Purchase>()
        val query =
                "SELECT $ALL_COLUMNS, " +
                       "${StoreItemDao.ALL_COLUMNS}, " +
                       "${ProductDao.ALL_COLUMNS}, " +
                       "${CountryDao.ALL_COLUMNS} " +
                "FROM $TABLE " +
                "INNER JOIN ${StoreItemDao.TABLE} ON $COLUMN_STORE_ITEM_ID = ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_ID} " +
                "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                "WHERE strftime('%Y', $COLUMN_TIMESTAMP) = '$year' AND strftime('%m', $COLUMN_TIMESTAMP) = '$month' " +
                "ORDER BY ${ProductDao.TABLE}.${ProductDao.COLUMN_ID}, ${StoreItemDao.COLUMN_ORGANIC}, ${StoreItemDao.COLUMN_PACKAGED}, ${CountryDao.TABLE}.${CountryDao.COLUMN_ID};"

        dbManager.select(query){
            var index = 0

            do {
                val purchase = producePurchase(it)

                if (index != 0 && results[index - 1].storeItem == purchase.storeItem) {
                    results[index - 1].weight += purchase.weight
                    results[index - 1].quantity += purchase.quantity
                } else {
                    results.add(purchase)
                    index++
                }
            } while (it.moveToNext())
        }

        return results
    }

    fun loadAlternativeEmission(purchases: List<Purchase>): Double {
        val emissions = mutableListOf<Double>()

        for (purchase in purchases) {
            val query =
                    "SELECT MIN(${EmissionCalculator.sqlEmissionFormula()}) " +
                            "FROM ${StoreItemDao.TABLE} " +
                            "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                            "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                            "WHERE ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} = ${purchase.storeItem.product.id};"

            dbManager.select(query) {
                val tempp = it.getDouble(0)
                val temp = purchase.weight * tempp
                emissions.add(temp)
            }
        }

        return emissions.sum()
    }

    fun generatePurchases(text: Text): MutableList<Purchase>{
        val purchases = mutableListOf<Purchase>()

        val iterator = text.textBlocks.iterator()
        var endReached = false

        while (!endReached && iterator.hasNext()) {
            endReached = generatePurchasesFromBlockLines(iterator.next().lines, purchases)
        }

        return purchases
    }

    private fun generatePurchasesFromBlockLines(lines: List<Text.Line>, purchases: MutableList<Purchase>): Boolean {
        var i = 0
        val limit = lines.size

        while (i < limit) {
            val (current, next) = getCurrentAndNext(lines, i, limit)

            if (endReached(current)) {
                return true
            }

            if (isValid(current)) {
                val quantity = extractQuantity(next)

                purchases.add(generatePurchase(current, quantity.toInt()))
            }
            i++
        }

        return false
    }

    private fun getCurrentAndNext(lines: List<Text.Line>, i: Int, limit: Int): Pair<String, String> {
        val current = lines[i].text.toLowerCase(Locale.getDefault())
        val next =
                if (i + 1 < limit)
                    lines[i + 1].text.toLowerCase(Locale.getDefault())
                else ""

        return Pair(current, next)
    }

    private fun endReached(receiptText: String): Boolean {
        return receiptText.contains("total")
    }

    private fun extractQuantity(receiptText: String) : String {
        val regex = "[0-9]+[x][0-9]+[,][0-9]+".toRegex()
        val find = regex.find(receiptText.replace(" ", ""))

        return buildString {
            if (find != null) {
                for (char in find.value) {
                    if (!char.isDigit()) {
                        break
                    }

                    this.append(char)
                }
            } else {
                this.append(0)
            }
        }
    }

    private fun isValid(receiptText: String): Boolean {
        return !(receiptText.contains("pant") || receiptText.contains("rabat") || receiptText.contains("*") || receiptText.contains("[0-9]+[,][0-9]+".toRegex()) || receiptText.length == 1)
    }

    private fun generatePurchase(receiptText: String, quantity: Int = 0): Purchase {
        val storeItem = StoreItemDao(dbManager).generateStoreItem(receiptText)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        //TODO: Extract quantity
        return Purchase(storeItem, sdf.format(Calendar.getInstance().time), quantity)
    }

    fun savePurchases(purchases: List<Purchase>): Boolean {
        val isValid = isValid(purchases)

        if(isValid) {
            for (purchase in purchases) {
                savePurchase(purchase)
            }
        }

        return isValid
    }

    private fun isValid(purchases: List<Purchase>): Boolean {
        for (purchase in purchases) {
            if (!purchase.isValid()) {
                return false
            }
        }

        return true
    }

    private fun savePurchase(purchase: Purchase){
        val storeItemId = StoreItemDao(dbManager).saveOrLoadStoreItem(purchase.storeItem);
        val contentValues = ContentValues()

        contentValues.put(COLUMN_STORE_ITEM_ID, storeItemId.toString())
        contentValues.put(COLUMN_TIMESTAMP, purchase.timestamp)
        contentValues.put(COLUMN_QUANTITY, purchase.quantity)

        dbManager.insert(TABLE, contentValues)
    }

    companion object {
        const val TABLE = "purchase"
        private const val COLUMN_COUNT = 3 // Excluding foreign keys
        const val COLUMN_STORE_ITEM_ID = "storeItemID"

        const val COLUMN_ID = "id"
        private const val COLUMN_ID_POSITION = 0

        const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_TIMESTAMP_POSITION = 1

        const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_QUANTITY_POSITION = 2

        const val ALL_COLUMNS =
                "$TABLE.$COLUMN_ID, " +                //0
                "$TABLE.$COLUMN_TIMESTAMP, " +         //1
                  "$TABLE.$COLUMN_QUANTITY"       //2

        fun producePurchase(cursor: Cursor, startIndex: Int = 0): Purchase{
            val storeItem = StoreItemDao.produceStoreItem(cursor, startIndex + COLUMN_COUNT)

            return Purchase(cursor.getInt(startIndex + COLUMN_ID_POSITION), storeItem, cursor.getString(startIndex + COLUMN_TIMESTAMP_POSITION), cursor.getInt(startIndex + COLUMN_QUANTITY_POSITION))
        }
    }
}