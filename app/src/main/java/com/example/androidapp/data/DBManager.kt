package com.example.androidapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager: SQLiteOpenHelper {

    constructor(context: Context?) : super(context, "FoodEmission.db", null, 1)

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        insertProductData()
        insertCountryData()

    }

    private fun createTables(db: SQLiteDatabase) {
        /*val createUserTableStmnt = "CREATE TABLE user(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, password TEXT NOT NULL, created DATE NOT NULL);"
        db.execSQL(createUserTableStmnt)*/

        val createCountryTableStmnt = "CREATE TABLE country(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, transportEmission REAL NOT NULL, GHPenalty BOOLEAN NOT NULL CHECK(GHPenalty IN (0, 1)));"
        db.execSQL(createCountryTableStmnt)

        val createProductTableStmnt = "CREATE TABLE product(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, cultivation REAL NOT NULL, iluc REAL NOT NULL, processing REAL NOT NULL, packaging REAL NOT NULL, retail REAL NOT NULL, GHCultivated BOOLEAN NOT NULL CHECK(GHCultivated IN (0, 1)));"
        db.execSQL(createProductTableStmnt)

        val createStoreItemTableStmnt = "CREATE TABLE storeItem(id INTEGER PRIMARY KEY AUTOINCREMENT, productID INTEGER NOT NULL, countryID INTEGER NOT NULL, receiptText TEXT NOT NULL, organic BOOLEAN NOT NULL CHECK(organic IN (0, 1)), packaged BOOLEAN NOT NULL CHECK(packaged IN (0, 1)), weight REAL NOT NULL, store TEXT NOT NULL, FOREIGN KEY(productID) REFERENCES product(id), FOREIGN KEY(countryID) REFERENCES country(id));"
        db.execSQL(createStoreItemTableStmnt)

        val createPurchaseTableStmnt = "CREATE TABLE purchase(id INTEGER PRIMARY KEY AUTOINCREMENT, storeItemID INTEGER NOT NULL, quantity INTEGER NOT NULL, timestamp DATETIME NOT NULL, FOREIGN KEY(productID) REFERENCES product(id));"
        db.execSQL(createPurchaseTableStmnt)
    }

    private fun insertCountryData() {
        insertCountry("Italien", 0.62, false)
        insertCountry("Polen", 0.32, true)
        insertCountry("Spanien", 0.74, false)
        insertCountry("Holland", 0.26, true)
        insertCountry("Danmark", 0.05, true)
        insertCountry("Marokko", 0.55, false)
    }

    private fun insertCountry(name: String, emission: Double, GHPenalty: Boolean) : Long {
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("name", name)
        contentValues.put("transportEmission", emission)
        contentValues.put("GHPenalty", GHPenalty)

        return db.insert("country", null, contentValues)
    }

    private fun insertProductData() {
        insertProduct("Tomat",0.07,0.01,0.0,0.14,0.01,true)
        insertProduct("Agurk", 0.05,0.01,0.0,0.14,0.01, true)
        insertProduct("Salat", 0.08, 0.02,0.0,0.06,0.01,false)
        insertProduct("Rødkål",0.1, 0.02, 0.0,0.06,0.01,false)
        insertProduct("Hvidkål",0.1, 0.02, 0.0,0.06,0.01,false)
        insertProduct("Æble",0.18,0.02,0.0,0.14,0.01,false)
        insertProduct("Spidskål",0.1,0.02,0.0,0.06,0.01,false)
        insertProduct("Blomkål",0.15,0.04,0.0,0.06,0.01,false)
        insertProduct("Champignon",0.01,0.01,0.0,0.26,0.0,true)
        insertProduct("Peberfrugt", 0.25,0.03,0.0,0.14,0.01,true)
        insertProduct("Broccoli",0.15,0.04,0.0,0.06,0.01,false)
        insertProduct("Gulerod",0.11,0.02,0.0,0.06,0.01,false)
    }

    private  fun insertProduct(name: String, cultivation: Double, iluc: Double, processing: Double, packaging: Double, retail: Double, GHCultivated: Boolean ): Long {
        val db: SQLiteDatabase = this.writableDatabase
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





    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}