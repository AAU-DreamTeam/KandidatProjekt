package androidapp.CO2Mad.models.daos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidapp.CO2Mad.models.tools.DBManager
import androidapp.CO2Mad.models.tools.EmissionCalculator
import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.models.enums.PRODUCT_CATEGORY
import java.util.*

class StoreItemDao(private val dbManager: DBManager) {
    constructor(context: Context) : this(DBManager(context))

    fun loadAlternatives(storeItem: StoreItem): List<StoreItem> {
        val ids = buildString {
            var first = true
            this.append("(")
            storeItem.altEmissions!!.forEach{
                if (first) {
                    this.append(it.first)
                    first = false
                } else {
                    this.append(", ${it.first}")
                }
            }
            this.append(")")
        }
        val query =
            "SELECT $ALL_COLUMNS, " +           // 5
                    "${ProductDao.ALL_COLUMNS}, " +            // 12
                    "${CountryDao.ALL_COLUMNS} " +            // 17
                    "FROM $TABLE " +
                    "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON ${TABLE}.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "WHERE ${TABLE}.$COLUMN_ID IN $ids;"

        val alternatives = dbManager.selectMultiple(query) {
            val alternative = produceStoreItem(it)
            alternative.rate(storeItem)
            alternative
        }

        return alternatives.sortedBy { it.emissionPerKg }
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
                    "$COLUMN_RECEIPT_TEXT = '${formatReceiptText(storeItem.receiptText)}';"

        return dbManager.select<Long>(query) {
            it.getLong(0)
        } ?: dbManager.INVALID_ID
    }

    fun saveStoreItem(storeItem: StoreItem): Long {
        val id = loadId(storeItem)

        return if (id != dbManager.INVALID_ID) {
            update(storeItem, id)
            id
        } else {
            save(storeItem)
        }
    }

    private fun update(storeItem: StoreItem, id: Long) {
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

        dbManager.update(TABLE, contentValues, COLUMN_ID, id.toString())
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

    fun loadAltEmissions(storeItem: StoreItem, numberOfAlternatives: Int) {
        if (storeItem.product.productCategory == PRODUCT_CATEGORY.VEGETABLES) {
            loadAltEmissionsForProduct(storeItem)
        } else {
            loadAltEmissionsForCategory(storeItem, numberOfAlternatives)
        }
    }

    private fun loadAltEmissionsForProduct(storeItem: StoreItem) {
        val query =
            "SELECT $ALL_COLUMNS, " +
                    "MIN(${EmissionCalculator.sqlEmissionPerKgFormula()}) AS EMISSION, " +
                    "${ProductDao.ALL_COLUMNS}, " +
                    "${CountryDao.ALL_COLUMNS} " +
                    "FROM $TABLE " +
                    "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON $TABLE.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "WHERE $COLUMN_PRODUCT_ID = ${storeItem.product.id} " +
                    "GROUP BY $TABLE.$COLUMN_ORGANIC, $TABLE.$COLUMN_PACKAGED " +
                    "HAVING EMISSION < ${storeItem.emissionPerKg} " +
                    "ORDER BY EMISSION ASC;"

        storeItem.altEmissions = dbManager.selectMultiple(query) {
            Pair(it.getInt(COLUMN_ID_POSITION), it.getDouble(COLUMN_COUNT))
        }

        storeItem.rate()
    }

    private fun loadAltEmissionsForCategory(storeItem: StoreItem, numberOfAlternatives: Int) {
        val emissions = mutableListOf<Pair<Int, Double>>()

        emissions.addAll(loadNonVeganAlternativeEmissions(storeItem))
        emissions.addAll(loadVeganAlternativeEmissions(storeItem, numberOfAlternatives - emissions.size))

        emissions.sortBy { it.second }

        storeItem.altEmissions = emissions
        storeItem.rate()
    }

    private fun loadNonVeganAlternativeEmissions(storeItem: StoreItem): List<Pair<Int, Double>> {
        val query =
            if (storeItem.product.productCategory == PRODUCT_CATEGORY.GRAINLEGUME) {
                "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} NOT IN (${PRODUCT_CATEGORY.VEGETABLES.ordinal}, ${PRODUCT_CATEGORY.GRAINLEGUME.ordinal}) ${PRODUCT_CATEGORY.VEGETABLES.ordinal} AND EMISSION < ${storeItem.emissionPerKg}")} " +
                "UNION " +
                "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} != ${PRODUCT_CATEGORY.VEGETABLES.ordinal} AND EMISSION < ${storeItem.emissionPerKg}")};"
            } else {
                "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} = ${storeItem.product.productCategory.ordinal} AND EMISSION < ${storeItem.emissionPerKg}")} " +
                "UNION " +
                "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} NOT IN (${storeItem.product.productCategory.ordinal}, ${PRODUCT_CATEGORY.VEGETABLES.ordinal}, ${PRODUCT_CATEGORY.GRAINLEGUME.ordinal}) AND EMISSION < ${storeItem.emissionPerKg}")};"
            }

        return dbManager.selectMultiple(query) {
            Pair(it.getInt(COLUMN_ID_POSITION), it.getDouble(COLUMN_COUNT))
        }
    }

    private fun loadVeganAlternativeEmissions(storeItem: StoreItem, limit: Int): List<Pair<Int, Double>> {
        val query = "${generateQuery("${ProductDao.TABLE}.${ProductDao.COLUMN_PRODUCT_CATEGORY} = ${PRODUCT_CATEGORY.GRAINLEGUME.ordinal} AND EMISSION < ${storeItem.emissionPerKg} ", limit)};"
        return dbManager.selectMultiple(query) {
            Pair(it.getInt(COLUMN_ID_POSITION), it.getDouble(COLUMN_COUNT))
        }
    }

    private fun generateQuery(condition: String, limit: Int = 1): String {
        return "SELECT * FROM (SELECT $ALL_COLUMNS, " +
                "${EmissionCalculator.sqlEmissionPerKgFormula()} AS EMISSION, " +
                "${ProductDao.ALL_COLUMNS}, " +
                "${CountryDao.ALL_COLUMNS} " +
                "FROM $TABLE " +
                "INNER JOIN ${ProductDao.TABLE} ON $COLUMN_PRODUCT_ID = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                "INNER JOIN ${CountryDao.TABLE} ON $TABLE.$COLUMN_COUNTRY_ID = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                "WHERE $condition " +
                "ORDER BY RANDOM() " +
                "LIMIT $limit)"
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

        fun produceStoreItem(cursor: Cursor, startIndex: Int = 0): StoreItem {
            val product = ProductDao.produceProduct(cursor, startIndex + COLUMN_COUNT)
            val country = CountryDao.produceCountry(
                cursor,
                startIndex + COLUMN_COUNT + ProductDao.COLUMN_COUNT
            )

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
                cursor.getString(startIndex + COLUMN_STORE_POSITION)
            )
        }
    }

    fun close() {
        dbManager.close()
    }
}
