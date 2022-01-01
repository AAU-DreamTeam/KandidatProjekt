package com.example.androidapp.models.tools

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper;
import java.util.*

class DBManager(context: Context?) : SQLiteOpenHelper(context, "FoodEmission.db", null, 1) {
    val INVALID_ID = -1L

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        insertProductData(db)
        insertCountryData(db)
        insertStoreItemData(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON;")

        val createCountryTableStmnt = "CREATE TABLE country(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, transportEmission REAL NOT NULL, GHPenalty BOOLEAN NOT NULL CHECK(GHPenalty IN (0, 1)));"
        db.execSQL(createCountryTableStmnt)

        val createProductTableStmnt = "CREATE TABLE product(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, cultivation REAL NOT NULL, iluc REAL NOT NULL, processing REAL NOT NULL, packaging REAL NOT NULL, retail REAL NOT NULL, GHCultivated BOOLEAN NOT NULL CHECK(GHCultivated IN (0, 1)));"
        db.execSQL(createProductTableStmnt)

        val createStoreItemTableStmnt = "CREATE TABLE storeItem(id INTEGER PRIMARY KEY AUTOINCREMENT, productID INTEGER NOT NULL, countryID INTEGER NOT NULL, receiptText TEXT NOT NULL, organic BOOLEAN NOT NULL CHECK(organic IN (0, 1)), packaged BOOLEAN NOT NULL CHECK(packaged IN (0, 1)), weight REAL NOT NULL, store TEXT NOT NULL, FOREIGN KEY(productID) REFERENCES product(id), FOREIGN KEY(countryID) REFERENCES country(id));"
        db.execSQL(createStoreItemTableStmnt)

        val createPurchaseTableStmnt = "CREATE TABLE purchase(id INTEGER PRIMARY KEY AUTOINCREMENT, storeItemID INTEGER NOT NULL, timestamp TEXT NOT NULL, quantity INTEGER NOT NULL, FOREIGN KEY(storeItemID) REFERENCES storeItem(id));"
        db.execSQL(createPurchaseTableStmnt)
    }

    private fun insertCountryData(db: SQLiteDatabase) {
        insertCountry(db, "Italien", 0.3184644, false)
        insertCountry(db, "Polen", 0.1625874, true)
        insertCountry(db, "Spanien", 0.3771804, false)
        insertCountry(db, "Holland", 0.1346274, true)
        insertCountry(db, "Danmark", 0.02796, true)
        insertCountry(db, "Marokko", 1.3174058, false)
    }

    private fun insertCountry(db: SQLiteDatabase, name: String, emission: Double, GHPenalty: Boolean) : Long {
        val contentValues = ContentValues()

        contentValues.put("name", name)
        contentValues.put("transportEmission", emission)
        contentValues.put("GHPenalty", GHPenalty)

        return db.insert("country", null, contentValues)
    }

    private fun insertProductData(db: SQLiteDatabase) {
        insertProduct(db, "Tomat",0.07,0.01,0.0,0.14,0.01,true)
        insertProduct(db, "Agurk", 0.05,0.01,0.0,0.14,0.01, true)
        insertProduct(db, "Salat", 0.08, 0.02,0.0,0.06,0.01,false)
        insertProduct(db, "Rødkål",0.1, 0.02, 0.0,0.06,0.01,false)
        insertProduct(db, "Hvidkål",0.1, 0.02, 0.0,0.06,0.01,false)
        insertProduct(db, "Æble",0.18,0.02,0.0,0.14,0.01,false)
        insertProduct(db, "Spidskål",0.1,0.02,0.0,0.06,0.01,false)
        insertProduct(db, "Blomkål",0.15,0.04,0.0,0.06,0.01,false)
        insertProduct(db, "Champignon",0.01,0.01,0.0,0.26,0.0,true)
        insertProduct(db, "Peberfrugt", 0.25,0.03,0.0,0.14,0.01,true)
        insertProduct(db, "Broccoli",0.15,0.04,0.0,0.06,0.01,false)
        insertProduct(db, "Gulerod",0.11,0.02,0.0,0.06,0.01,false)
    }

    private  fun insertProduct(db: SQLiteDatabase, name: String, cultivation: Double, iluc: Double, processing: Double, packaging: Double, retail: Double, GHCultivated: Boolean ): Long {
        val contentValues = ContentValues()

        contentValues.put("name", name)
        contentValues.put("cultivation", cultivation)
        contentValues.put("iluc", iluc)
        contentValues.put("processing", processing)
        contentValues.put("packaging", packaging)
        contentValues.put("retail", retail)
        contentValues.put("GHCultivated", GHCultivated)

        return db.insert("product", null, contentValues)
    }

    private fun insertStoreItemData(db: SQLiteDatabase){
        insertStoreItem(db, 6, 1,"ebler lose italien", false, false, 0.110)
        insertStoreItem(db, 6, 5,"ebler lose danmark", false, false, 0.110)
        insertStoreItem(db, 6, 5,"ebler oko danmark", true, true, 1.0)
        insertStoreItem(db, 6, 1,"oko bakke ebler", true, true, 0.660)
        insertStoreItem(db, 6, 1,"oko. pose ebler", true, true, 1.0)
        insertStoreItem(db, 6, 5,"danske ebler", false, true, 1.5)
        insertStoreItem(db, 6, 5,"oko. danske bakke ebler", true, true, 0.660)
        insertStoreItem(db, 6, 5,"danske ebler", false, true, 0.880)
        insertStoreItem(db, 6, 1,"oko. pink lady", true, true, 0.312)
        insertStoreItem(db, 6, 5,"pigeon ebler", false, true, 0.750)
        insertStoreItem(db, 6, 5,"danske belle de boskoop", false, true, 0.660)
        insertStoreItem(db, 6, 1,"pink lady ebler", false, true, 1.0)

        insertStoreItem(db, 11, 3,"broccoli", false, true, 0.450)
        insertStoreItem(db, 11, 1,"oko danske broccoli", true, true, 0.450)

        insertStoreItem(db, 9, 5,"oko champignon hvide, skar", true, true, 0.100)
        insertStoreItem(db, 9, 5,"oko champignon skiver/mix", true, true, 0.100)
        insertStoreItem(db, 9, 5,"oko champignon hvide", true, true, 0.200)
        insertStoreItem(db, 9, 5,"oko champignon brune", true, true, 0.400)
        insertStoreItem(db, 9, 2,"champignon rodskarne", false, true, 0.500)

        insertStoreItem(db, 10, 6,"snackpeber sod rod", false, true, 0.500)
        insertStoreItem(db, 10, 4,"mix snack peber", false, true, 0.400)
        insertStoreItem(db, 10, 3,"oko peber california", true, true, 0.300)
        insertStoreItem(db, 10, 3,"lamuyo peber", false, true, 0.600)
        insertStoreItem(db, 10, 4,"lose peberfrugter", false, false, 0.180)
        insertStoreItem(db, 10, 5,"rod peber", false, true, 0.360)

        insertStoreItem(db, 12, 5,"oko gulerodder", true, true, 0.800)
        insertStoreItem(db, 12, 5,"danske gulerodder", false, true, 1.0)
        insertStoreItem(db, 12, 5,"oko. danske gulerodder", true, true, 1.0)
        insertStoreItem(db, 12, 5,"gulerodder", false, true, 1.0)
        insertStoreItem(db, 12, 5,"orangerodder", false, true, 0.500)
        insertStoreItem(db, 12, 5,"orangerodder", true, true, 0.250)
        insertStoreItem(db, 12, 5,"orangerodder", false, true, 0.200)

        insertStoreItem(db, 1, 6,"tomater i spand", false, true, 0.500)
        insertStoreItem(db, 1, 5,"oko tomater san marzano", true, true, 0.200)
        insertStoreItem(db, 1, 3,"oko tomater cherry", true, true, 0.250)
        insertStoreItem(db, 1, 5,"oko tomater intensity", true, true, 0.250)
        insertStoreItem(db, 1, 4,"intensity tom. princip", false, true, 0.300)
        insertStoreItem(db, 1, 5,"oko runde tomater", true, true, 0.500)
        insertStoreItem(db, 1, 6,"cherrytomater mix", false, true, 0.500)
        insertStoreItem(db, 1, 4,"gusto tomater", false, true, 0.450)
        insertStoreItem(db, 1, 3,"COCKTAILTOMAT", false, true, 0.500)
        insertStoreItem(db, 1, 5,"danske mix tomater", false, true, 0.325)
        insertStoreItem(db, 1, 6,"lose tomater", false, false, 0.075)
        insertStoreItem(db, 1, 5,"san marzano", false, true, 0.250)
        insertStoreItem(db, 1, 5,"tomater ida", false, true, 0.200)
        insertStoreItem(db, 1, 5,"blommetomater", false, true, 0.500)
        insertStoreItem(db, 1, 5,"oko intensity gule", true, true, 0.250)
        insertStoreItem(db, 1, 3,"oko tomater allure", true, true, 0.200)

        insertStoreItem(db, 3, 3,"oko iceberg levevis", true, true, 0.400)
        insertStoreItem(db, 3, 3,"iceberg", false, true, 0.400)

        insertStoreItem(db, 8, 1,"oko blomkal", true, true, 0.450)
        insertStoreItem(db, 8, 1,"blomkal", false, false, 0.450)

        insertStoreItem(db, 4, 5,"oko rodkal", true, true, 1.5)
        insertStoreItem(db, 4, 5,"rodkal", false, true, 1.5)

        insertStoreItem(db, 5, 5,"oko hvidkal", true, true, 1.5)
        insertStoreItem(db, 5, 5,"danske hvidkal", false, false, 1.5)

        insertStoreItem(db, 7, 5,"oko spidskal", true, true, 0.700)
        insertStoreItem(db, 7, 5,"oko spidskal rod", true, true, 0.700)

        insertStoreItem(db, 2, 3,"udl. agurk", false, false, 0.300)
        insertStoreItem(db, 2, 3,"oko agurk levevis", true, true, 0.300)
        insertStoreItem(db, 2, 5,"oko agurk dk", true, true, 0.300)
        insertStoreItem(db, 2, 3,"skoleagurker", false, true, 0.300)
    }

    private fun insertStoreItem(db: SQLiteDatabase, productID: Int, countryID: Int, receiptText: String, organic: Boolean, packaged: Boolean, weight: Double, store: String = "Føtex"): Long {
        val contentValues = ContentValues()

        contentValues.put("productID", productID)
        contentValues.put("countryID", countryID)
        contentValues.put("receiptText", receiptText.toUpperCase(Locale.getDefault()))
        contentValues.put("organic", organic)
        contentValues.put("packaged", packaged)
        contentValues.put("weight", weight)
        contentValues.put("store", store)

        return db.insert("storeItem", null, contentValues)
    }

    fun insert(table: String, contentValues: ContentValues): Long {
        return writableDatabase.insert(table, null, contentValues)
    }

    fun <T> selectMultiple(query: String, producer: (cursor: Cursor) -> T): List<T> {
        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        val results: MutableList<T> = mutableListOf()

        if (cursor.moveToFirst()) {
            do {
                results.add(producer(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()

        return results
    }

    fun select(query: String, producer: (cursor: Cursor) -> (Unit)) {
        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            producer(cursor)
        }

        cursor.close()
    }

    fun booleanToInt(boolean: Boolean): Int {
        return if (boolean) 1 else 0
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}