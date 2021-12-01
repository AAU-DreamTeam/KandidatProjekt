package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Product

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
                "SELECT  $COLUMN_ID, " +                // 6
                        "$COLUMN_NAME, " +              // 7
                        "$COLUMN_CULTIVATION, " +       // 8
                        "$COLUMN_ILUC, " +              // 9
                        "$COLUMN_PROCESSING, " +        // 10
                        "$COLUMN_PACKAGING, " +         // 11
                        "$COLUMN_RETAIL, " +            // 12
                        "$COLUMN_GHCULTIVATED " +      // 13
                "FROM $TABLE " +
                "WHERE '$receiptText' LIKE '%'||REPLACE(REPLACE(REPLACE(LOWER($COLUMN_NAME), 'å', 'a'), 'æ', 'e'), 'ø', 'o')||'%';"

        dbManager.select(query) {
            result = produceProduct(it)
        }

        return result
    }

    companion object {
        val TABLE = "product"
        val COLUMN_COUNT = 8

        val COLUMN_ID = "$TABLE.id"
        val COLUMN_ID_POSITION = 0

        val COLUMN_NAME = "$TABLE.name"
        val COLUMN_NAME_POSITION = 1

        val COLUMN_CULTIVATION = "$TABLE.cultivation"
        val COLUMN_CULTIVATION_POSITION = 2

        val COLUMN_ILUC = "$TABLE.iluc"
        val COLUMN_ILUC_POSITION = 3

        val COLUMN_PROCESSING = "$TABLE.processing"
        val COLUMN_PROCESSING_POSITION = 4

        val COLUMN_PACKAGING = "$TABLE.packaging"
        val COLUMN_PACKAGING_POSITION = 5

        val COLUMN_RETAIL = "$TABLE.retail"
        val COLUMN_RETAIL_POSITION = 6

        val COLUMN_GHCULTIVATED = "$TABLE.GHCultivated"
        val COLUMN_GHCULTIVATED_POSITION = 7

        fun produceProduct(cursor: Cursor, startIndex: Int = 0): Product {
            return Product(
                    cursor.getInt(startIndex + COLUMN_ID_POSITION),
                    cursor.getString(startIndex + COLUMN_NAME_POSITION),
                    cursor.getDouble(startIndex + COLUMN_CULTIVATION_POSITION),
                    cursor.getDouble(startIndex + COLUMN_ILUC_POSITION),
                    cursor.getDouble(startIndex + COLUMN_PROCESSING_POSITION),
                    cursor.getDouble(startIndex + COLUMN_PACKAGING_POSITION),
                    cursor.getDouble(startIndex + COLUMN_RETAIL_POSITION),
                    cursor.getInt(startIndex + COLUMN_GHCULTIVATED_POSITION) != 0
            )
        }
    }
}