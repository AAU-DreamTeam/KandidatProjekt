package com.example.androidapp.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.models.tools.DBManager
import com.example.androidapp.models.Product

class ProductDao(private val dbManager: DBManager) {
    constructor(context: Context): this(DBManager(context))

    fun loadProducts(): List<Product>{
        val query = "SELECT * FROM $TABLE;"

        return dbManager.selectMultiple(query) {
            produceProduct(it)
        }
    }

    fun extractProduct(receiptText: String): Product {
        var result = Product()
        val query =
                "SELECT * FROM $TABLE " +
                "WHERE '$receiptText' LIKE '%'||REPLACE(REPLACE(REPLACE(LOWER($COLUMN_NAME), 'å', 'a'), 'æ', 'e'), 'ø', 'o')||'%';"

        dbManager.select(query) {
            result = produceProduct(it)
        }

        return result
    }

    companion object {
        const val TABLE = "product"
        const val COLUMN_COUNT = 10

        const val COLUMN_ID = "id"
        private const val COLUMN_ID_POSITION = 0

        const val COLUMN_NAME = "name"
        private const val COLUMN_NAME_POSITION = 1

        const val COLUMN_CULTIVATION = "cultivation"
        private const val COLUMN_CULTIVATION_POSITION = 2

        const val COLUMN_ILUC = "iluc"
        private const val COLUMN_ILUC_POSITION = 3

        const val COLUMN_PROCESSING = "processing"
        private const val COLUMN_PROCESSING_POSITION = 4

        const val COLUMN_PACKAGING = "packaging"
        private const val COLUMN_PACKAGING_POSITION = 5

        const val COLUMN_RETAIL = "retail"
        private const val COLUMN_RETAIL_POSITION = 6

        const val COLUMN_GHCULTIVATED = "GHCultivated"
        private const val COLUMN_GHCULTIVATED_POSITION = 7

        const val COLUMN_COUNTRYID = "countryId"
        private const val COLUMN_COUNTRYID_POSITION = 8

        const val COLUMN_WEIGHT = "weight"
        private const val COLUMN_WEIGHT_POSITION = 9

        const val ALL_COLUMNS =
                "$TABLE.$COLUMN_ID, " +                // 6
                "$TABLE.$COLUMN_NAME, " +              // 7
                "$TABLE.$COLUMN_CULTIVATION, " +       // 8
                "$TABLE.$COLUMN_ILUC, " +              // 9
                "$TABLE.$COLUMN_PROCESSING, " +        // 10
                "$TABLE.$COLUMN_PACKAGING, " +         // 11
                "$TABLE.$COLUMN_RETAIL, " +            // 12
                "$TABLE.$COLUMN_GHCULTIVATED, "+      // 13
                "$TABLE.$COLUMN_COUNTRYID, "+      // 14
                "$TABLE.$COLUMN_WEIGHT "      // 15


        fun produceProduct(cursor: Cursor, startIndex: Int = 0): Product {
            return Product(
                    cursor.getInt(startIndex + COLUMN_ID_POSITION),
                    cursor.getString(startIndex + COLUMN_NAME_POSITION),
                    cursor.getDouble(startIndex + COLUMN_CULTIVATION_POSITION),
                    cursor.getDouble(startIndex + COLUMN_ILUC_POSITION),
                    cursor.getDouble(startIndex + COLUMN_PROCESSING_POSITION),
                    cursor.getDouble(startIndex + COLUMN_PACKAGING_POSITION),
                    cursor.getDouble(startIndex + COLUMN_RETAIL_POSITION),
                    cursor.getInt(startIndex + COLUMN_GHCULTIVATED_POSITION) != 0,
                    cursor.getInt(startIndex + COLUMN_COUNTRYID_POSITION),
                    cursor.getDouble(startIndex + COLUMN_WEIGHT_POSITION)

            )
        }
    }

    fun close() {
        dbManager.close()
    }
}