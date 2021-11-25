package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Product

class ProductDao(context: Context) {
    private val dbManager: DBManager = DBManager(context)

    companion object {
        fun produceProduct(cursor: Cursor, startIndex: Int = 0): Product {
            return Product(
                    cursor.getInt(startIndex),
                    cursor.getString(startIndex + 1),
                    cursor.getDouble(startIndex + 2),
                    cursor.getDouble(startIndex + 3),
                    cursor.getDouble(startIndex + 4),
                    cursor.getDouble(startIndex + 5),
                    cursor.getDouble(startIndex + 6),
                    cursor.getInt(startIndex + 7) != 0
            )
        }
    }
}