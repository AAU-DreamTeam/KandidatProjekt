package com.example.androidapp.models.tools

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper;
import com.example.androidapp.models.enums.PRODUCT_CATEGORY
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

        val createProductTableStmnt = "CREATE TABLE product(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, productCategory INTEGER NOT NULL, cultivation REAL NOT NULL, iluc REAL NOT NULL, processing REAL NOT NULL, packaging REAL NOT NULL, retail REAL NOT NULL, GHCultivated BOOLEAN NOT NULL CHECK(GHCultivated IN (0, 1)),countryID INTEGER NOT NULL,weight REAL NOT NULL);"
        db.execSQL(createProductTableStmnt)

        val createStoreItemTableStmnt = "CREATE TABLE storeItem(id INTEGER PRIMARY KEY AUTOINCREMENT, productID INTEGER NOT NULL, countryID INTEGER NOT NULL, receiptText TEXT NOT NULL, organic BOOLEAN NOT NULL CHECK(organic IN (0, 1)), packaged BOOLEAN NOT NULL CHECK(packaged IN (0, 1)), weight REAL NOT NULL, store TEXT NOT NULL, FOREIGN KEY(productID) REFERENCES product(id), FOREIGN KEY(countryID) REFERENCES country(id));"
        db.execSQL(createStoreItemTableStmnt)

        val createPurchaseTableStmnt = "CREATE TABLE purchase(id INTEGER PRIMARY KEY AUTOINCREMENT, storeItemID INTEGER NOT NULL, timestamp TEXT NOT NULL, week INTEGER NOT NULL, quantity INTEGER NOT NULL, FOREIGN KEY(storeItemID) REFERENCES storeItem(id));"
        db.execSQL(createPurchaseTableStmnt)


    }

    private fun insertCountryData(db: SQLiteDatabase) {
        insertCountry(db, "Italien", 0.3184644, false)//1
        insertCountry(db, "Polen", 0.1625874, true)//2
        insertCountry(db, "Spanien", 0.3771804, false)//3
        insertCountry(db, "Holland", 0.1346274, true)//4
        insertCountry(db, "Danmark", 0.02796, true)//5
        insertCountry(db, "Marokko", 1.3174058, false)//6
        insertCountry(db, "Ecuador",1.9488758 , false)//7
        insertCountry(db, "Kina",3.2554076 , false)//8
        insertCountry(db, "Peru",1.9488758 , false)//9
        insertCountry(db, "Tyskland",0.113238 , true)//10
        insertCountry(db, "England",0.2601678 , true)//11
        insertCountry(db, "Turkey",1.0170902 , false)//12
        insertCountry(db, "USA",1.2230248 , true)//14
        insertCountry(db, "Indien",2.0416272 , false)//15
        insertCountry(db, "New Zealand",2.3757766 , false)//16
        insertCountry(db, "Brasilien",1.9488758 , false)//17
    }

    private fun insertCountry(db: SQLiteDatabase, name: String, emission: Double, GHPenalty: Boolean) : Long {
        val contentValues = ContentValues()

        contentValues.put("name", name)
        contentValues.put("transportEmission", emission)
        contentValues.put("GHPenalty", GHPenalty)

        return db.insert("country", null, contentValues)
    }

    private fun insertProductData(db: SQLiteDatabase) {
        insertProduct(db, "Agurk", PRODUCT_CATEGORY.VEGETABLES, 0.05, 0.01, 0.00, 0.14, 0.01, true,4,500.0)
        insertProduct(db, "Ananas", PRODUCT_CATEGORY.VEGETABLES, 0.09, 0.02, 0.00, 0.00, 0.01, true,7,1200.0)
        insertProduct(db, "And", PRODUCT_CATEGORY.POULTRY, 1.60, 0.43, 0.00, 0.14, 0.00, false,4,300.0)
        insertProduct(db, "Appelsin", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, true,3,160.0)
        insertProduct(db, "Artiskok", PRODUCT_CATEGORY.VEGETABLES, 0.27, 0.05, 0.00, 0.06, 0.01, false,1,150.0)
        insertProduct(db, "Asparges, grønne", PRODUCT_CATEGORY.VEGETABLES, 0.27, 0.10, 0.00, 0.06, 0.01, false,3,500.0)
        insertProduct(db, "Aubergine", PRODUCT_CATEGORY.VEGETABLES, 0.16, 0.02, 0.00, 0.14, 0.01, true,8,250.0)
        insertProduct(db, "Avocado", PRODUCT_CATEGORY.VEGETABLES, 0.32, 0.06, 0.00, 0.14, 0.01, true,9,150.0)
        insertProduct(db, "Banan", PRODUCT_CATEGORY.VEGETABLES, 0.21, 0.02, 0.00, 0.14, 0.01, true,7,110.0)
        insertProduct(db, "Basilikum", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.06, 0.01, false,4,100.0)
        insertProduct(db, "Bladselleri", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.06, 0.01, false,4,300.0)
        insertProduct(db, "Blomkål", PRODUCT_CATEGORY.VEGETABLES, 0.15, 0.04, 0.00, 0.06, 0.01, false,3,450.0)
        insertProduct(db, "Blomme", PRODUCT_CATEGORY.VEGETABLES, 0.27, 0.10, 0.00, 0.14, 0.01, false,4,500.0)
        insertProduct(db, "Blåbær", PRODUCT_CATEGORY.VEGETABLES, 1.76, 0.94, 0.00, 0.14, 0.01, false,9,125.0)
        insertProduct(db, "Broccoli", PRODUCT_CATEGORY.VEGETABLES, 0.15, 0.04, 0.00, 0.06, 0.01, false,3,350.0)
        insertProduct(db, "Brombær", PRODUCT_CATEGORY.VEGETABLES, 0.31, 0.14, 0.00, 0.14, 0.01, false,2,125.0)
        insertProduct(db, "Bønnespirer", PRODUCT_CATEGORY.VEGETABLES, 0.20, 0.04, 0.00, 0.14, 0.01, true,8,150.0)
        insertProduct(db, "Champignon", PRODUCT_CATEGORY.VEGETABLES, 0.01, 0.01, 0.00, 0.26, 0.00, false,10,500.0)
        insertProduct(db, "Citron", PRODUCT_CATEGORY.VEGETABLES, 0.09, 0.03, 0.00, 0.14, 0.01, true,3,85.0)
        insertProduct(db, "Dild", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.14, 0.01, false,4,20.0)
        insertProduct(db, "Fennikel", PRODUCT_CATEGORY.VEGETABLES, 3.30, 0.95, 0.00, 0.14, 0.01, false,4,200.0)
        insertProduct(db, "Fersken", PRODUCT_CATEGORY.VEGETABLES, 0.11, 0.03, 0.00, 0.14, 0.01, false,1,100.0)
        insertProduct(db, "Forårsløg", PRODUCT_CATEGORY.VEGETABLES, 0.18, 0.04, 0.00, 0.06, 0.01, false,11,200.0)
        insertProduct(db, "Grapefrugt", PRODUCT_CATEGORY.VEGETABLES, 0.09, 0.02, 0.00, 0.14, 0.01, true,8,350.0)
        insertProduct(db, "Grisefilet", PRODUCT_CATEGORY.PORK, 3.33, 0.58, -0.26, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Grisekød, hakket", PRODUCT_CATEGORY.PORK, 2.50, 0.39, -0.25, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Grisekød, nakkefilet", PRODUCT_CATEGORY.PORK, 3.22, 0.50, -0.41, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Grisekød, nakkekam med svær", PRODUCT_CATEGORY.PORK, 2.78, 0.48, -0.22, 0.14, 0.00, false,4,2400.0)
        insertProduct(db, "Grisekød, svinekam med svær", PRODUCT_CATEGORY.PORK, 2.78, 0.48, -0.22, 0.14, 0.00, false,4,2400.0)
        insertProduct(db, "Svinemørbrad", PRODUCT_CATEGORY.PORK, 4.45, 0.77, -0.35, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Græskar", PRODUCT_CATEGORY.VEGETABLES, 0.14, 0.04, 0.00, 0.06, 0.01, false,8,1000.0)
        insertProduct(db, "Grønkål", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, false,4,250.0)
        insertProduct(db, "Grønne bønner", PRODUCT_CATEGORY.GRAINLEGUME, 0.20, 0.04, 0.00, 0.14, 0.01, true,8,250.0)
        insertProduct(db, "Grønne linser", PRODUCT_CATEGORY.GRAINLEGUME, 0.85, 0.33, 0.00, 0.20, 0.01, false,12,400.0)
        insertProduct(db, "Grøntsagsbøffer", PRODUCT_CATEGORY.GRAINLEGUME, 0.34, 0.10, 0.33, 0.24, 0.03, false,4,226.0)
        insertProduct(db, "Gulerod", PRODUCT_CATEGORY.VEGETABLES, 0.11, 0.02, 0.00, 0.06, 0.01, false,4,1000.0)
        insertProduct(db, "Gås", PRODUCT_CATEGORY.POULTRY, 1.60, 0.43, 0.00, 0.14, 0.00, false,4,2500.0)
        insertProduct(db, "Hakket kylling", PRODUCT_CATEGORY.POULTRY, 2.96, 0.73, -0.22, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Hakket lammekød", PRODUCT_CATEGORY.POULTRY, 24.01, 6.26, -4.22, 0.14, 0.00, false,16,500.0)
        insertProduct(db, "Hindbær", PRODUCT_CATEGORY.VEGETABLES, 0.27, 0.11, 0.00, 0.14, 0.01, false,2,250.0)
        insertProduct(db, "Honningmelon", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.00, 0.01,false,17,800.0)
        insertProduct(db, "Hvidkål", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, false,4,1000.0)
        insertProduct(db, "Hvidløg", PRODUCT_CATEGORY.VEGETABLES, 0.51, 0.05, 0.00, 0.06, 0.01, false,3,180.0)
        insertProduct(db, "Hyben", PRODUCT_CATEGORY.VEGETABLES, 0.19, 0.03, 0.00, 0.14, 0.01, false,12,250.0)
        insertProduct(db, "Hyldebær", PRODUCT_CATEGORY.VEGETABLES, 0.31, 0.14, 0.00, 0.14, 0.01, false,2,250.0)
        insertProduct(db, "Høne", PRODUCT_CATEGORY.POULTRY, 1.60, 0.43, -0.06, 0.14, 0.00, false,4,1000.0)
        insertProduct(db, "Ingefær, rod", PRODUCT_CATEGORY.VEGETABLES, 0.97, 0.05, 0.00, 0.14, 0.01, true,8,200.0)
        insertProduct(db, "Jordbær", PRODUCT_CATEGORY.VEGETABLES, 0.15, 0.06, 0.00, 0.14, 0.01, false,4,400.0)
        insertProduct(db, "Kalkun", PRODUCT_CATEGORY.POULTRY, 2.40, 0.61, -0.11, 0.14, 0.00, false,4,800.0)
        insertProduct(db, "Kalkunkød, hakket", PRODUCT_CATEGORY.POULTRY, 2.24, 0.60, 0.26, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Kalv og flæsk, hakket", PRODUCT_CATEGORY.BEEF, 12.88, 2.47, -0.35, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Kalvekød", PRODUCT_CATEGORY.BEEF, 43.77, 8.48, -2.66, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Karse", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.14, 0.01, false,4,100.0)
        insertProduct(db, "Kartoffel", PRODUCT_CATEGORY.VEGETABLES, 0.20, 0.02, 0.00, 0.06, 0.01, false,4,1000.0)
        insertProduct(db, "Kidney bønner", PRODUCT_CATEGORY.GRAINLEGUME, 0.14, 0.05, 0.47, 0.22, 0.01, false,8,400.0)
        insertProduct(db, "Kikærter, tørrede", PRODUCT_CATEGORY.GRAINLEGUME, 0.93, 0.52, 0.00, 0.20, 0.01, false,15,400.0)
        insertProduct(db, "Kirsebær", PRODUCT_CATEGORY.VEGETABLES, 0.26, 0.07, 0.00, 0.14, 0.01, false,12,500.0)
        insertProduct(db, "Kiwi", PRODUCT_CATEGORY.VEGETABLES, 0.16, 0.03, 0.00, 0.14, 0.01, true,1,500.0)
        insertProduct(db, "Kylling, ben", PRODUCT_CATEGORY.POULTRY, 0.93, 0.25, -0.03, 0.14, 0.00, false,4,800.0)
        insertProduct(db, "Kylling, bryst", PRODUCT_CATEGORY.POULTRY, 2.59, 0.69, -0.09, 0.14, 0.00, false,4,450.0)
        insertProduct(db, "Kylling, hel", PRODUCT_CATEGORY.POULTRY, 1.60, 0.43, -0.06, 0.14, 0.00, false,4,1350.0)
        insertProduct(db, "Kylling, lår", PRODUCT_CATEGORY.POULTRY, 0.88, 0.24, -0.03, 0.14, 0.00, false,4,700.0)
        insertProduct(db, "Lammekølle", PRODUCT_CATEGORY.LAMB, 25.35, 6.58, -5.12, 0.14, 0.00, false,16,1300.0)
        insertProduct(db, "Lammekød", PRODUCT_CATEGORY.LAMB, 25.35, 6.58, -5.12, 0.14, 0.00, false,16,500.0)
        insertProduct(db, "Lime", PRODUCT_CATEGORY.VEGETABLES, 0.09, 0.03, 0.00, 0.06, 0.01, false,3,200.0)
        insertProduct(db, "Løg", PRODUCT_CATEGORY.VEGETABLES, 0.18, 0.04, 0.00, 0.06, 0.01, false,11,1000.0)
        insertProduct(db, "Majroe", PRODUCT_CATEGORY.VEGETABLES, 0.11, 0.02, 0.00, 0.06, 0.01, false,4,1000.0)
        insertProduct(db, "Majskolbe", PRODUCT_CATEGORY.VEGETABLES, 0.21, 0.07, 0.00, 0.14, 0.01, false,4,400.0)
        insertProduct(db, "Mandarin", PRODUCT_CATEGORY.VEGETABLES, 0.08, 0.02, 0.00, 0.14, 0.01, true,3,300.0)
        insertProduct(db, "Mango", PRODUCT_CATEGORY.VEGETABLES, 0.18, 0.06, 0.00, 0.14, 0.01, true,15,300.0)
        insertProduct(db, "Medisterpølse", PRODUCT_CATEGORY.PORK, 1.57, 0.27, 0.13, 0.14, 0.00, false,4,500.0)
        insertProduct(db, "Nektarin", PRODUCT_CATEGORY.VEGETABLES, 0.11, 0.03, 0.00, 0.14, 0.01, false,1,115.0)
        insertProduct(db, "Okseculotte", PRODUCT_CATEGORY.BEEF, 39.65, 7.68, -2.41, 0.14, 0.00, false,4,450.0)
        insertProduct(db, "Oksekød, hakket", PRODUCT_CATEGORY.BEEF, 28.58, 5.46, -1.78, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Okseinderlår", PRODUCT_CATEGORY.BEEF, 39.65, 7.68, -2.41, 0.14, 0.00, false,4,1000.0)
        insertProduct(db, "Oksemørbrad", PRODUCT_CATEGORY.BEEF, 133.45, 25.85, -8.11, 0.14, 0.00, false,4,1500.0)
        insertProduct(db, "Oksetyndsteg", PRODUCT_CATEGORY.BEEF, 70.72, 13.70, -4.30, 0.14, 0.00, false,4,2000.0)
        insertProduct(db, "Chili", PRODUCT_CATEGORY.VEGETABLES, 0.25, 0.03, 0.00, 0.14, 0.01, true,8,70.0)
        insertProduct(db, "Peberfrugt", PRODUCT_CATEGORY.VEGETABLES, 0.25, 0.03, 0.00, 0.14, 0.01, true,3,400.0)
        insertProduct(db, "Persille", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.14, 0.01, false,4,50.0)
        insertProduct(db, "Persillerod", PRODUCT_CATEGORY.VEGETABLES, 0.23, 0.07, 0.00, 0.06, 0.01, false,4,500.0)
        insertProduct(db, "Porre", PRODUCT_CATEGORY.VEGETABLES, 0.16, 0.04, 0.00, 0.06, 0.01, false,4,150.0)
        insertProduct(db, "Portobello", PRODUCT_CATEGORY.VEGETABLES, 0.01, 0.01, 0.00, 0.14, 0.01, false,10,250.0)
        insertProduct(db, "Purløg", PRODUCT_CATEGORY.VEGETABLES, 0.16, 0.04, 0.00, 0.06, 0.01, false,4,100.0)
        insertProduct(db, "Pære", PRODUCT_CATEGORY.VEGETABLES, 0.15, 0.03, 0.00, 0.14, 0.01, false,4,150.0)
        insertProduct(db, "Quinoa, sort", PRODUCT_CATEGORY.GRAINLEGUME, 1.04, 0.63, 0.00, 0.20, 0.01, false,9,300.0)
        insertProduct(db, "Rabarber", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.06, 0.01, false,4,100.0)
        insertProduct(db, "Radise", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.14, 0.01, false,4,250.0)
        insertProduct(db, "Ribs", PRODUCT_CATEGORY.VEGETABLES, 0.25, 0.08, 0.00, 0.14, 0.01, false,4,125.0)
        insertProduct(db, "Rosenkål", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, false,4,350.0)
        insertProduct(db, "Rødbede", PRODUCT_CATEGORY.VEGETABLES, 0.17, 0.01, 0.00, 0.06, 0.01, false,4,1000.0)
        insertProduct(db, "Røde linser", PRODUCT_CATEGORY.GRAINLEGUME, 0.85, 0.33, 0.00, 0.20, 0.01, false,12,400.0)
        insertProduct(db, "Rødkål", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, false,4,100.0)
        insertProduct(db, "Icebergsalat", PRODUCT_CATEGORY.VEGETABLES, 0.08, 0.02, 0.00, 0.06, 0.01, false,3,400.0)
        insertProduct(db, "Savoykål", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, false,4,800.0)
        insertProduct(db, "Knoldselleri", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.06, 0.01, false,4,800.0)
        insertProduct(db, "Skinkeschnitzel", PRODUCT_CATEGORY.PORK, 2.78, 0.48, -0.22, 0.14, 0.00, false,4,400.0)
        insertProduct(db, "Sojabønner, tørrede", PRODUCT_CATEGORY.GRAINLEGUME, 0.32, 0.27, 0.00, 0.20, 0.01, false,8,1000.0)
        insertProduct(db, "Solbær", PRODUCT_CATEGORY.VEGETABLES, 0.25, 0.08, 0.00, 0.14, 0.01, false,4,150.0)
        insertProduct(db, "Sorte bønner", PRODUCT_CATEGORY.GRAINLEGUME, 0.14, 0.05, 0.47, 0.22, 0.01, false,8,250.0)
        insertProduct(db, "Spidskål", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, false,4,700.0)
        insertProduct(db, "Spinat", PRODUCT_CATEGORY.VEGETABLES, 0.12, 0.03, 0.00, 0.06, 0.01, false,4,200.0)
        insertProduct(db, "Squash", PRODUCT_CATEGORY.VEGETABLES, 0.14, 0.04, 0.00, 0.06, 0.01, true,8,280.0)
        insertProduct(db, "Stikkelsbær", PRODUCT_CATEGORY.VEGETABLES, 0.21, 0.09, 0.00, 0.14, 0.01, false,10,250.0)
        insertProduct(db, "Tomat", PRODUCT_CATEGORY.VEGETABLES, 0.07, 0.01, 0.00, 0.14, 0.01, true,3,500.0)
        insertProduct(db, "Tranebær", PRODUCT_CATEGORY.VEGETABLES, 0.15, 0.03, 0.00, 0.14, 0.01, false,14,250.0)
        insertProduct(db, "Vandmelon", PRODUCT_CATEGORY.VEGETABLES, 0.03, 0.01, 0.00, 0.00, 0.01, false,3,700.0)
        insertProduct(db, "Vegansk fars", PRODUCT_CATEGORY.GRAINLEGUME, 0.15, 0.15, 0.09, 0.14, 0.00, false,4,350.0)
        insertProduct(db, "Vindrue", PRODUCT_CATEGORY.VEGETABLES, 0.22, 0.05, 0.00, 0.14, 0.01, true,1,500.0)
        insertProduct(db, "Æble", PRODUCT_CATEGORY.VEGETABLES, 0.18, 0.02, 0.00, 0.14, 0.01, false,4,150.0)
        insertProduct(db, "Ærter", PRODUCT_CATEGORY.VEGETABLES, 0.36, 0.12, 0.00, 0.06, 0.01, false,4,400.0)
    }

    private  fun insertProduct(db: SQLiteDatabase, name: String, productCategory: PRODUCT_CATEGORY, cultivation: Double, iluc: Double, processing: Double, packaging: Double, retail: Double, GHCultivated: Boolean, countryID: Int, weight: Double ): Long {
        val contentValues = ContentValues()

        contentValues.put("name", name)
        contentValues.put("productCategory", productCategory.ordinal)
        contentValues.put("cultivation", cultivation)
        contentValues.put("iluc", iluc)
        contentValues.put("processing", processing)
        contentValues.put("packaging", packaging)
        contentValues.put("retail", retail)
        contentValues.put("GHCultivated", GHCultivated)
        contentValues.put("countryId", countryID)
        contentValues.put("weight", weight)

        return db.insert("product", null, contentValues)
    }

    private fun insertStoreItemData(db: SQLiteDatabase){
        /*insertStoreItem(db, 6, 1,"ebler lose italien", false, false, 0.110)
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
        insertStoreItem(db, 1, 3,"cocktailtomat", false, true, 0.500)
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
        insertStoreItem(db, 2, 3,"skoleagurker", false, true, 0.300)*/
    }

    private fun insertStoreItem(db: SQLiteDatabase, productID: Int, countryID: Int, receiptText: String, organic: Boolean, packaged: Boolean, weight: Double, store: String = "Føtex"): Long {
        val contentValues = ContentValues()

        contentValues.put("productID", productID)
        contentValues.put("countryID", countryID)
        contentValues.put("receiptText", receiptText)
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

    fun <T> select(query: String, producer: (cursor: Cursor) -> T): T? {
        val db = readableDatabase
        val cursor = db.rawQuery(query, null)
        var result: T? = null

        if (cursor.moveToFirst()) {
            result = producer(cursor)
        }
        cursor.close()

        return result
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
