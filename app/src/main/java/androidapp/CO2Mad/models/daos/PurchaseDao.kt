package androidapp.CO2Mad.models.daos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidapp.CO2Mad.models.Purchase
import androidapp.CO2Mad.models.Trip
import androidapp.CO2Mad.models.tools.DBManager
import androidapp.CO2Mad.models.tools.EmissionCalculator
import androidapp.CO2Mad.models.tools.TextRecognizer
import com.google.mlkit.vision.text.Text
import java.text.SimpleDateFormat
import java.util.*

class PurchaseDao(context: Context) {
    private val dbManager = DBManager(context)

    fun loadEmissionFromYearMonth(calendar: Calendar): Double {
        val month = SimpleDateFormat("MM", Locale.getDefault()).format(calendar.time)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.time)
        val query =
            "SELECT SUM(${EmissionCalculator.sqlEmissionPerPurchaseFormula()}), " +
                    "${StoreItemDao.ALL_COLUMNS}, " +
                    "${ProductDao.ALL_COLUMNS}, " +
                    "${CountryDao.ALL_COLUMNS} " +
                    "FROM $TABLE " +
                    "INNER JOIN ${StoreItemDao.TABLE} ON $COLUMN_STORE_ITEM_ID = ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_ID} " +
                    "INNER JOIN ${ProductDao.TABLE} ON  ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "WHERE strftime('%Y', $COLUMN_TIMESTAMP) = '$year' AND strftime('%m', $COLUMN_TIMESTAMP) = '$month' " +
                    "ORDER BY ${ProductDao.TABLE}.${ProductDao.COLUMN_ID}, ${StoreItemDao.COLUMN_ORGANIC}, ${StoreItemDao.COLUMN_PACKAGED}, ${CountryDao.TABLE}.${CountryDao.COLUMN_ID};"

        return dbManager.select<Double>(query) {
            it.getDouble(0)
        }!!
    }

    fun loadEmissionFromYearWeek(calendar: Calendar): Double {
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.time)
        val query =
            "SELECT SUM(${EmissionCalculator.sqlEmissionPerPurchaseFormula()}), " +
                    "${StoreItemDao.ALL_COLUMNS}, " +
                    "${ProductDao.ALL_COLUMNS}, " +
                    "${CountryDao.ALL_COLUMNS} " +
                    "FROM $TABLE " +
                    "INNER JOIN ${StoreItemDao.TABLE} ON $COLUMN_STORE_ITEM_ID = ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_ID} " +
                    "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "WHERE strftime('%Y', $COLUMN_TIMESTAMP) = '$year' AND $COLUMN_WEEK = $week " +
                    "ORDER BY ${ProductDao.TABLE}.${ProductDao.COLUMN_ID}, ${StoreItemDao.COLUMN_ORGANIC}, ${StoreItemDao.COLUMN_PACKAGED}, ${CountryDao.TABLE}.${CountryDao.COLUMN_ID};"

        return dbManager.select<Double>(query) {
            it.getDouble(0)
        }!!
    }

    fun extractPurchases(imagePath: String, callback: (MutableList<Purchase>) -> Unit) {
        TextRecognizer().runTextRecognition(imagePath) {
            extractPurchasesFromText(it, callback)
        }
    }

    private fun extractPurchasesFromText(text: Text, callback: (MutableList<Purchase>) -> Unit) {
        val purchases = mutableListOf<Purchase>()
        val iterator = text.textBlocks.iterator()
        var endReached = false

        while (!endReached && iterator.hasNext()) {
            endReached = extractPurchasesFromBlock(iterator.next().lines, purchases)
        }

        callback(purchases)
    }

    private fun extractPurchasesFromBlock(lines: List<Text.Line>, purchases: MutableList<Purchase>): Boolean {
        var i = 0
        val limit = lines.size

        while (i < limit) {
            val current = lines[i].text

            if (endReached(current)) {
                return true
            }

            if (isValid(current)) {
                val quantity = extractQuantity(lines, i, limit)

                purchases.add(extractPurchaseFromLine(current, quantity.toInt()))
            }
            i++
        }

        return false
    }

    private fun getNext(lines: List<Text.Line>, i: Int, limit: Int): String {
        val next =
                if (i + 1 < limit)
                    lines[i + 1].text
                else ""

        return next
    }

    private fun endReached(receiptText: String): Boolean {
        return receiptText.contains("TOTAL")
    }

    private fun extractQuantity(lines: List<Text.Line>, i: Int, limit: Int) : String {
        val next = getNext(lines, i, limit)
        val regex = "[0-9]+[xX][0-9]+[,][0-9]+".toRegex()
        val find = regex.find(next.replace(" ", ""))

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
        return !(receiptText.contains("PANT") || receiptText.contains("RABAT") || receiptText.contains("*") || !receiptText.contains("([a-z]|[A-Z])+".toRegex()) || receiptText.contains("[0-9]+[,][0-9]+".toRegex()) || receiptText.length == 1)
    }

    private fun extractPurchaseFromLine(receiptText: String, quantity: Int = 0): Purchase {
        val storeItem = StoreItemDao(dbManager).extractStoreItem(receiptText)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Copenhagen"))

        return Purchase(storeItem, calendar, quantity)
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
        val storeItemId = StoreItemDao(dbManager).saveStoreItem(purchase.storeItem);
        val contentValues = ContentValues()

        contentValues.put(COLUMN_STORE_ITEM_ID, storeItemId.toString())
        contentValues.put(COLUMN_TIMESTAMP, purchase.timestamp)
        contentValues.put(COLUMN_WEEK, purchase.week)
        contentValues.put(COLUMN_QUANTITY, purchase.quantity)

        dbManager.insert(TABLE, contentValues)
    }

    companion object {
        const val TABLE = "purchase"
        private const val COLUMN_COUNT = 4 // Excluding foreign keys
        const val COLUMN_STORE_ITEM_ID = "storeItemID"

        const val COLUMN_ID = "id"
        private const val COLUMN_ID_POSITION = 0

        const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_TIMESTAMP_POSITION = 1

        const val COLUMN_WEEK = "week"
        private const val COLUMN_WEEK_POSITION = 2

        const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_QUANTITY_POSITION = 3

        const val ALL_COLUMNS =
                "$TABLE.$COLUMN_ID, " +                //0
                "$TABLE.$COLUMN_TIMESTAMP, " +         //1
                "$TABLE.$COLUMN_WEEK, " +
                "$TABLE.$COLUMN_QUANTITY"       //3

        private fun producePurchase(cursor: Cursor, startIndex: Int = 0): Purchase{
            val storeItem = StoreItemDao.produceStoreItem(cursor,startIndex + COLUMN_COUNT)
            val timestamp = cursor.getString(startIndex + COLUMN_TIMESTAMP_POSITION)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            calendar.time = sdf.parse(timestamp)!!

            return Purchase(cursor.getInt(startIndex + COLUMN_ID_POSITION), storeItem, calendar, cursor.getInt(startIndex + COLUMN_QUANTITY_POSITION))
        }

        fun produceTrip(cursor: Cursor): Pair<List<Trip>, List<Purchase>> {
            val trips = mutableListOf<Trip>()
            val allPurchases = mutableListOf<Purchase>()
            var timestamp = cursor.getString(COLUMN_TIMESTAMP_POSITION)
            var purchases = mutableListOf<Purchase>()

            do {
                val purchase = producePurchase(cursor)
                val t = purchase.timestamp

                if (t != timestamp) {
                    trips.add(Trip(purchases))
                    timestamp = purchase.timestamp
                    purchases = mutableListOf()
                }
                purchases.add(purchase)
                allPurchases.add(purchase)
            } while (cursor.moveToNext())

            trips.add(Trip(purchases))

            return Pair(trips, allPurchases)
        }
    }

    fun close() {
        dbManager.close()
    }

    fun loadAllTrips(numberOfAlternatives: Int): Pair<List<Trip>, List<Purchase>> {
        val query =
            "SELECT $ALL_COLUMNS, " +
                    "${StoreItemDao.ALL_COLUMNS}, " +
                    "${ProductDao.ALL_COLUMNS}, " +
                    "${CountryDao.ALL_COLUMNS} " +
                    "FROM $TABLE " +
                    "INNER JOIN ${StoreItemDao.TABLE} ON $COLUMN_STORE_ITEM_ID = ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_ID} " +
                    "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "ORDER BY $TABLE.$COLUMN_TIMESTAMP DESC;"

        val (trips, purchases) = dbManager.select<Pair<List<Trip>, List<Purchase>>>(query) {
            produceTrip(it)
        } ?: Pair(listOf(), listOf())

        getAltEmissions(trips, numberOfAlternatives)

        return Pair(trips, purchases)
    }

    private fun getAltEmissions(trips: List<Trip>, numberOfAlternatives: Int){
        val storeItemDao = StoreItemDao(dbManager)

        for (trip in trips) {
            for (purchase in trip.purchases) {
                storeItemDao.loadAltEmissions(purchase.storeItem, numberOfAlternatives)
            }
        }
    }
}