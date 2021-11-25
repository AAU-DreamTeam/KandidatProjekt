package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Product

class ProductDao(context: Context) {
    private val dbManager: DBManager = DBManager(context)

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