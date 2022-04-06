package com.example.androidapp.models.daos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.androidapp.models.tools.DBManager
import com.example.androidapp.models.tools.EmissionCalculator
import com.example.androidapp.models.StoreItem
import java.util.*

class StoreItemDao(private val dbManager: DBManager) {
    constructor(context: Context): this(DBManager(context))

    fun loadStoreItems(): List<StoreItem> {
        val results = mutableListOf<StoreItem>()
        val query =
                "SELECT $ALL_COLUMNS, " +           // 5
                        "${ProductDao.ALL_COLUMNS}, " +            // 12
                        "${CountryDao.ALL_COLUMNS} " +            // 17
                "FROM $TABLE " +
                "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                "ORDER BY ${ProductDao.TABLE}.${ProductDao.COLUMN_NAME}, $TABLE.$COLUMN_ORGANIC, $TABLE.$COLUMN_PACKAGED, ${CountryDao.TABLE}.${CountryDao.COLUMN_NAME};"

        dbManager.select(query){
            var index = 0

            do {
                val storeItem = produceStoreItem(it)

                if (index == 0 || results[index - 1] != storeItem) {
                    results.add(storeItem)
                    index++
                }
            } while (it.moveToNext())
        }

        return results
    }

    fun loadAlternatives(storeItem: StoreItem) : List<StoreItem> {
        val query =
                "SELECT $ALL_COLUMNS, " +           // 5
                        "${ProductDao.ALL_COLUMNS}, " +            // 12
                        "${CountryDao.ALL_COLUMNS}, " +            // 17
                        "MIN(${EmissionCalculator.sqlEmissionPerKgFormula()}) " +
                "FROM $TABLE " +
                "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                "WHERE $COLUMN_PRODUCT_ID = ${storeItem.product.id} " +
                "GROUP BY $COLUMN_ORGANIC, $COLUMN_PACKAGED;"

        return dbManager.selectMultiple(query) {
            produceStoreItem(it)
        }
    }

    fun extractStoreItem(receiptText: String): StoreItem {
        var result: StoreItem? = null
        val formattedReceiptText = formatReceiptText(receiptText)
        val query =
                "SELECT $ALL_COLUMNS, " +           // 5
                       "${ProductDao.ALL_COLUMNS}, " +            // 12
                       "${CountryDao.ALL_COLUMNS} " +            // 17
                "FROM $TABLE " +
                "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.${COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                "WHERE $COLUMN_RECEIPT_TEXT = \"$formattedReceiptText\";"

        dbManager.select(query) {
            result = produceStoreItem(it)
        }

        if (result == null) {
            result = generateStoreItem(formattedReceiptText)
        }

        result!!.receiptText = receiptText

        return result as StoreItem
    }

    private fun formatReceiptText(receiptText: String): String {
        return receiptText.toLowerCase(Locale.getDefault()).replace('ø', 'o').replace('å', 'a').replace('æ','e')
    }

    private fun generateStoreItem(receiptText: String): StoreItem {
        val product = ProductDao(dbManager).extractProduct(receiptText)
        val country = CountryDao(dbManager).extractCountry(receiptText)
        return StoreItem(
                product,
                country,
                receiptText,
                isOrganic(receiptText),
                isPackaged(receiptText),
                extractWeight(receiptText).toDouble()
        )
    }

    private fun isOrganic(receiptText: String): Boolean {
        return receiptText.contains("oko")
    }

    private fun isPackaged(receiptText: String): Boolean {
        return !receiptText.contains("los")
    }

    private fun extractWeight(receiptText: String): Double{
        val regex = "[0-9]+([g]|[k][g])".toRegex()
        val find = regex.find(receiptText.replace(" ", ""))
        var inGrams = false

        val weight = buildString {
            if (find != null) {
                for (char in find.value) {
                    if (!char.isDigit()) {
                        break
                    }

                    this.append(char)
                }

                inGrams = !find.value.contains("kg")

            } else {
                this.append(0)
            }
        }.toDouble()

        return if (inGrams) {
            weight/1000
        } else {
            return weight
        }
    }

    private fun loadId(storeItem: StoreItem): Long{
        val query =
                "SELECT $TABLE.$COLUMN_ID " +
                "FROM $TABLE " +
                "WHERE $COLUMN_PRODUCT_ID = ${storeItem.product.id} AND " +
                      "$COLUMN_COUNTRY_ID = ${storeItem.country.id} AND " +
                      "$COLUMN_RECEIPT_TEXT = '${storeItem.receiptText}' AND " +
                      "$COLUMN_ORGANIC = ${dbManager.booleanToInt(storeItem.organic)} AND " +         // 2
                      "$COLUMN_PACKAGED =  ${dbManager.booleanToInt(storeItem.packaged)} AND " +        // 3
                      "$COLUMN_WEIGHT = ${storeItem.weight} AND " +          // 4
                      "$COLUMN_STORE = '${storeItem.store}';"           // 5

        return dbManager.select<Long>(query){
            it.getLong(0)
        }?: dbManager.INVALID_ID
    }

    fun saveStoreItem(storeItem: StoreItem): Long {
        val id = loadId(storeItem)

        return if (id != dbManager.INVALID_ID) {
            id
        } else {
            save(storeItem)
        }
    }

    private fun save(storeItem: StoreItem): Long {
        val contentValues = ContentValues()

        contentValues.put(COLUMN_PRODUCT_ID, storeItem.product.id)
        contentValues.put(COLUMN_COUNTRY_ID, storeItem.country.id)
        contentValues.put(COLUMN_RECEIPT_TEXT, formatReceiptText(storeItem.receiptText))
        contentValues.put(COLUMN_ORGANIC, storeItem.organic)
        contentValues.put(COLUMN_PACKAGED, storeItem.packaged)
        contentValues.put(COLUMN_WEIGHT, storeItem.weight)
        contentValues.put(COLUMN_STORE, storeItem.store)

        return dbManager.insert(TABLE, contentValues)
    }

    companion object{
        const val TABLE = "storeItem"
        const val COLUMN_COUNT = 6
        const val COLUMN_COUNTRY_ID = "countryID"
        const val COLUMN_PRODUCT_ID = "productID"

        const val COLUMN_ID = "id"
        const val COLUMN_ID_POSITION = 0

        const val COLUMN_RECEIPT_TEXT = "receiptText"
        const val COLUMN_RECEIPT_TEXT_POSITION = 1

        const val COLUMN_ORGANIC = "organic"
        const val COLUMN_ORGANIC_POSITION = 2

        const val COLUMN_PACKAGED = "packaged"
        const val COLUMN_PACKAGED_POSITION = 3

        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_WEIGHT_POSITION = 4

        const val COLUMN_STORE = "store"
        const val COLUMN_STORE_POSITION = 5

        const val ALL_COLUMNS =
                "$TABLE.$COLUMN_ID, " +
                "$TABLE.$COLUMN_RECEIPT_TEXT, " +
                "$TABLE.$COLUMN_ORGANIC, " +
                "$TABLE.$COLUMN_PACKAGED, " +
                "$TABLE.$COLUMN_WEIGHT, " +
                "$TABLE.$COLUMN_STORE"

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

    fun close() {
        dbManager.close()
    }
}