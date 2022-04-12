package androidapp.CO2Mad.models.daos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import androidapp.CO2Mad.models.tools.DBManager
import androidapp.CO2Mad.models.tools.EmissionCalculator
import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.models.enums.PRODUCT_CATEGORY
import androidapp.CO2Mad.models.enums.RATING
import java.util.*

class StoreItemDao(private val dbManager: DBManager) {
    constructor(context: Context) : this(DBManager(context))

    fun loadStoreItems(): List<StoreItem> {
        val results = mutableListOf<StoreItem>()
        val query =
            "SELECT $ALL_COLUMNS, " +           // 5
                    "${ProductDao.ALL_COLUMNS}, " +            // 12
                    "${CountryDao.ALL_COLUMNS} " +            // 17
                    "FROM $TABLE " +
                    "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON ${TABLE}.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "ORDER BY ${ProductDao.TABLE}.${ProductDao.COLUMN_NAME}, $TABLE.$COLUMN_ORGANIC, $TABLE.$COLUMN_PACKAGED, ${CountryDao.TABLE}.${CountryDao.COLUMN_NAME};"

        dbManager.select(query) {
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

    fun loadAlternatives(storeItem: StoreItem, numberOfAlternatives: Int): List<StoreItem> {
        return if (storeItem.product.productCategory == PRODUCT_CATEGORY.VEGETABLES) {
            loadAlternativesFromProduct(storeItem)
        } else {
            loadAlternativesFromCategories(storeItem, numberOfAlternatives)
        }
    }

    private fun loadAlternativesFromProduct(storeItem: StoreItem): List<StoreItem> {
        val query =
            "SELECT $ALL_COLUMNS, " +
                    "${ProductDao.ALL_COLUMNS}, " +
                    "${CountryDao.ALL_COLUMNS}, " +
                    "MIN(${EmissionCalculator.sqlEmissionPerKgFormula()}) AS EMISSION, " +
                    "rating.rating " +
                    "FROM $TABLE " +
                    "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON $TABLE.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "INNER JOIN ${EmissionCalculator.test()} rating ON $TABLE.$COLUMN_ID = rating.id " +
                    "WHERE $COLUMN_PRODUCT_ID = ${storeItem.product.id} " +
                    "GROUP BY $TABLE.$COLUMN_ORGANIC, $TABLE.$COLUMN_PACKAGED " +
                    "HAVING EMISSION < ${storeItem.emissionPerKg};"

        println("---" + query)

        val lst = dbManager.selectMultiple(query) {
            produceStoreItem(it, true, 1)
        }

        return lst
    }

    private fun loadAlternativesFromCategories(
        storeItem: StoreItem,
        numberOfAlternatives: Int
    ): List<StoreItem> {
        val storeItems = mutableListOf<StoreItem>()

        storeItems.addAll(loadNonVeganAlternatives(storeItem))
        storeItems.addAll(loadVeganAlternatives(storeItem, numberOfAlternatives - storeItems.size - 1))

        storeItems.sortBy { it.emissionPerKg }

        return storeItems
    }

    private fun loadNonVeganAlternatives(storeItem: StoreItem): List<StoreItem> {
        val query =
            "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} = ${storeItem.product.productCategory.ordinal} AND EMISSION < ${storeItem.emissionPerKg}")} " +
                    "UNION " +
                    "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} NOT IN (${storeItem.product.productCategory.ordinal}, ${PRODUCT_CATEGORY.VEGETABLES.ordinal}, ${PRODUCT_CATEGORY.GRAINLEGUME.ordinal}) AND EMISSION < ${storeItem.emissionPerKg}")};"

        println()
        return dbManager.selectMultiple(query) {
            produceStoreItem(it)
        }
    }

    private fun loadVeganAlternatives(
        storeItem: StoreItem,
        alternativesToGet: Int
    ): List<StoreItem> {
        val query = "${
            generateQuery(
                "${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} = ${PRODUCT_CATEGORY.GRAINLEGUME.ordinal} AND EMISSION < ${storeItem.emissionPerKg}",
                alternativesToGet
            )
        } " +
                "UNION " +
                "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_ID} = ${storeItem.altEmission.first}")};"

        return dbManager.selectMultiple(query) {
            produceStoreItem(it)
        }
    }

    private fun generateQuery(condition: String, limit: Int = 1): String {
        return "SELECT * FROM (SELECT $ALL_COLUMNS, " +
                "${ProductDao.ALL_COLUMNS}, " +
                "${CountryDao.ALL_COLUMNS}, " +
                "(${EmissionCalculator.sqlEmissionPerKgFormula()}) AS EMISSION " +
                "FROM $TABLE " +
                "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON $TABLE.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                "WHERE $condition " +
                "ORDER BY RANDOM() " +
                "LIMIT $limit)"
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
        return receiptText.toLowerCase(Locale.getDefault()).replace('ø', 'o').replace('å', 'a')
            .replace('æ', 'e')
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
            extractWeight(receiptText).toDouble(),
            false,
            false
        )
    }

    private fun isOrganic(receiptText: String): Boolean {
        return receiptText.contains("oko")
    }

    private fun isPackaged(receiptText: String): Boolean {
        return !receiptText.contains("los")
    }

    private fun extractWeight(receiptText: String): Double {
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
            weight / 1000
        } else {
            return weight
        }
    }

    private fun loadId(storeItem: StoreItem): Long {
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

        return dbManager.select<Long>(query) {
            it.getLong(0)
        } ?: dbManager.INVALID_ID
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
        contentValues.put(COLUMN_COUNTRY_DEFAULT,storeItem.countryDefault)
        contentValues.put(COLUMN_WEIGHT_DEFAULT,storeItem.weightDefault)
        contentValues.put(COLUMN_STORE, storeItem.store)

        return dbManager.insert(TABLE, contentValues)
    }

    fun loadAltEmission(storeItem: StoreItem) {
        val emission = EmissionCalculator.sqlEmissionPerKgFormula()
        val query = if (storeItem.product.productCategory == PRODUCT_CATEGORY.VEGETABLES) {
            "SELECT ${ProductDao.TABLE}.${ProductDao.COLUMN_ID}, MIN($emission) " +
                    "FROM $TABLE " +
                    "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON $TABLE.${COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "WHERE $COLUMN_PRODUCT_ID = ${storeItem.product.id};"
        } else {
            "SELECT ${ProductDao.TABLE}.${ProductDao.COLUMN_ID}, $emission AS ALT_EMISSION " +
                    "FROM $TABLE " +
                    "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON ${TABLE}.${COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "WHERE ALT_EMISSION < ${storeItem.emissionPerKg} AND ${ProductDao.COLUMN_PRODUCT_CATEGORY} = ${PRODUCT_CATEGORY.GRAINLEGUME.ordinal} AND ALT_EMISSION < (SELECT MIN($emission) FROM $TABLE INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} INNER JOIN ${CountryDao.TABLE} ON ${TABLE}.${COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} WHERE productCategory NOT IN (${PRODUCT_CATEGORY.VEGETABLES.ordinal}, ${PRODUCT_CATEGORY.GRAINLEGUME.ordinal})) " +
                    "ORDER BY RANDOM() " +
                    "LIMIT 1;"
        }

        println(EmissionCalculator.sqlRatingTable())

        storeItem.altEmission = dbManager.select<Pair<Int, Double>>(query) {
            Pair(it.getInt(0), it.getDouble(1))
        } ?: Pair(0, storeItem.emissionPerKg)
    }

    companion object {
        const val TABLE = "storeItem"
        const val COLUMN_COUNT = 8
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

        const val COLUMN_COUNTRY_DEFAULT = "countryDefault"
        const val COLUMN_COUNTRY_DEFAULT_POSITION = 5

        const val COLUMN_WEIGHT_DEFAULT = "weightDefault"
        const val COLUMN_WEIGHT_DEFAULT_POSITION = 6

        const val COLUMN_STORE = "store"
        const val COLUMN_STORE_POSITION = 7

        const val ALL_COLUMNS =
            "$TABLE.$COLUMN_ID, " +
                    "$TABLE.$COLUMN_RECEIPT_TEXT, " +
                    "$TABLE.$COLUMN_ORGANIC, " +
                    "$TABLE.$COLUMN_PACKAGED, " +
                    "$TABLE.$COLUMN_WEIGHT, " +
                    "$TABLE.$COLUMN_COUNTRY_DEFAULT, " +
                    "$TABLE.$COLUMN_WEIGHT_DEFAULT, " +
                    "$TABLE.$COLUMN_STORE"

        fun produceStoreItem(cursor: Cursor, withRating: Boolean = false, ratingOffset: Int = -1, startIndex: Int = 0): StoreItem {
            val product = ProductDao.produceProduct(cursor, startIndex + COLUMN_COUNT)
            val country = CountryDao.produceCountry(
                cursor,
                startIndex + COLUMN_COUNT + ProductDao.COLUMN_COUNT
            )

            val rating = if (withRating) RATING.values()[cursor.getInt(startIndex + COLUMN_COUNT + ProductDao.COLUMN_COUNT + CountryDao.COLUMN_COUNT + ratingOffset)] else null

            return StoreItem(
                cursor.getInt(startIndex + COLUMN_ID_POSITION),
                product,
                country,
                cursor.getString(startIndex + COLUMN_RECEIPT_TEXT_POSITION),
                cursor.getInt(startIndex + COLUMN_ORGANIC_POSITION) != 0,
                cursor.getInt(startIndex + COLUMN_PACKAGED_POSITION) != 0,
                cursor.getDouble(startIndex + COLUMN_WEIGHT_POSITION),
                cursor.getInt(startIndex + COLUMN_COUNTRY_DEFAULT_POSITION) !=0,
                cursor.getInt(startIndex + COLUMN_WEIGHT_DEFAULT_POSITION) !=0,
                cursor.getString(startIndex + COLUMN_STORE_POSITION),
                rating
            )
        }
    }

    fun close() {
        dbManager.close()
    }
}
