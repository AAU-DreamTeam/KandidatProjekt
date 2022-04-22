package androidapp.CO2Mad.models.tools

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper;
import androidapp.CO2Mad.models.daos.CountryDao
import androidapp.CO2Mad.models.daos.VariablesDao
import androidapp.CO2Mad.models.daos.ProductDao
import androidapp.CO2Mad.models.enums.PRODUCT_CATEGORY

class DBManager(context: Context?) : SQLiteOpenHelper(context, "FoodEmission.db", null, 1) {
    val INVALID_ID = -1L

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        insertProductData(db)
        insertCountryData(db)
        insertStoreItemData(db)
        insertVariables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON;")

        val createCountryTableStmnt = "CREATE TABLE country(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, transportEmission REAL NOT NULL, GHPenalty BOOLEAN NOT NULL CHECK(GHPenalty IN (0, 1)));"
        db.execSQL(createCountryTableStmnt)

        val createProductTableStmnt = "CREATE TABLE product(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, productCategory INTEGER NOT NULL, cultivation REAL NOT NULL, iluc REAL NOT NULL, processing REAL NOT NULL, packaging REAL NOT NULL, retail REAL NOT NULL, GHCultivated BOOLEAN NOT NULL CHECK(GHCultivated IN (0, 1)),countryID INTEGER NOT NULL,weight REAL NOT NULL);"
        db.execSQL(createProductTableStmnt)

        val createStoreItemTableStmnt = "CREATE TABLE storeItem(id INTEGER PRIMARY KEY AUTOINCREMENT, productID INTEGER NOT NULL, countryID INTEGER NOT NULL, receiptText TEXT NOT NULL, organic BOOLEAN NOT NULL CHECK(organic IN (0, 1)), packaged BOOLEAN NOT NULL CHECK(packaged IN (0, 1)), weight REAL NOT NULL,countryDefault BOOLEAN NOT NULL CHECK(countryDefault IN (0, 1)),weightDefault BOOLEAN NOT NULL CHECK(weightDefault IN (0, 1)), store TEXT NOT NULL, FOREIGN KEY(productID) REFERENCES product(id), FOREIGN KEY(countryID) REFERENCES country(id));"
        db.execSQL(createStoreItemTableStmnt)

        val createPurchaseTableStmnt = "CREATE TABLE purchase(id INTEGER PRIMARY KEY AUTOINCREMENT, storeItemID INTEGER NOT NULL, timestamp TEXT NOT NULL, week INTEGER NOT NULL, quantity INTEGER NOT NULL, FOREIGN KEY(storeItemID) REFERENCES storeItem(id));"
        db.execSQL(createPurchaseTableStmnt)

        val createHighScoreTableStmt = "CREATE TABLE ${VariablesDao.TABLE}(${VariablesDao.COLUMN_ID} INTEGER PRIMARY KEY, ${VariablesDao.COLUMN_SCORE} INTEGER NOT NULL, ${VariablesDao.COLUMN_ENABLE_GAME} BOOLEAN NOT NULL);"
        db.execSQL(createHighScoreTableStmt)
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
        insertCountry(db, "Tyrkiet",1.0170902 , false)//12
        insertCountry(db, "USA",1.2230248 , true)//14
        insertCountry(db, "Indien",2.0416272 , false)//15
        insertCountry(db, "New Zealand",2.3757766 , false)//16
        insertCountry(db, "Brasilien",1.9488758 , false)//17
        insertCountry(db, "Frankrig", 0.2524788, false)//18
        insertCountry(db, "Egypten", 1.3174058, false)//19
        insertCountry(db, "Sydafrika", 1.3174058, false)//20
        insertCountry(db, "Chile", 1.9488758, false)//21
        insertCountry(db, "Colombia", 1.9488758, false)//22
        insertCountry(db, "Israel", 1.3393066, false)//23
        insertCountry(db, "Mexico", 1.9488758, false)//24
        insertCountry(db, "Ecuador", 1.9488758, false)//25
        insertCountry(db, "Argentina", 1.9488758, false)//26
        insertCountry(db, "Belgien", 0.1616088, true)//27
        insertCountry(db, "Portugal", 0.02796, false)//28
        insertCountry(db, "Mozambique", 1.3174058, false)//29
        insertCountry(db, "Kenya", 1.3174058, false)//30
        insertCountry(db, "Senegal", 1.3174058, false)//31
        insertCountry(db, "Costa Rica", 1.9488758, false)//32
        insertCountry(db, "Vietnam", 2.3757766, false)//33
        insertCountry(db, "Den Dominikanske Republik", 1.9488758, false)//34
        insertCountry(db, "Østrig", 0.21669, true)//35
        insertCountry(db, "Namibia", 1.3174058, false)//36
        insertCountry(db, "Guatemala", 1.9488758, false)//37
        insertCountry(db, "Zimbabwe", 1.3174058, false)//38
        insertCountry(db, "Bulgarien", 0.0849031172, false)//39
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
        insertProduct(db, "Asparges", PRODUCT_CATEGORY.VEGETABLES, 0.27, 0.10, 0.00, 0.06, 0.01, false,3,500.0)
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
        insertProduct(db, "Linser, tørrede", PRODUCT_CATEGORY.GRAINLEGUME, 0.85, 0.33, 0.00, 0.20, 0.01, false,12,400.0)
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
        insertProduct(db, "Quinoa", PRODUCT_CATEGORY.GRAINLEGUME, 1.04, 0.63, 0.00, 0.20, 0.01, false,9,300.0)
        insertProduct(db, "Rabarber", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.06, 0.01, false,4,100.0)
        insertProduct(db, "Radise", PRODUCT_CATEGORY.VEGETABLES, 0.13, 0.03, 0.00, 0.14, 0.01, false,4,250.0)
        insertProduct(db, "Ribs", PRODUCT_CATEGORY.VEGETABLES, 0.25, 0.08, 0.00, 0.14, 0.01, false,4,125.0)
        insertProduct(db, "Rosenkål", PRODUCT_CATEGORY.VEGETABLES, 0.10, 0.02, 0.00, 0.06, 0.01, false,4,350.0)
        insertProduct(db, "Rødbede", PRODUCT_CATEGORY.VEGETABLES, 0.17, 0.01, 0.00, 0.06, 0.01, false,4,1000.0)
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
        insertProduct(db, "Tofu", PRODUCT_CATEGORY.GRAINLEGUME, 0.18, 0.11, 0.91, 0.26, 0.00, false, 39, 200.0)
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
        // Agurk
        insertStoreItem(db, "Agurk", "Danmark", "dk agurk", false, true, 1 * 0.3)
        insertStoreItem(db, "Agurk", "Danmark", "agurk dansk oko danmark", true, true, 1 * 0.3)
        insertStoreItem(db, "Agurk", "Spanien", "agurk oko spanien", true, true, 1 * 0.3)

// Ananas
        insertStoreItem(db, "Ananas", "Ecuador", "ananas ecuador", false, false, 1 * 1.200)

// And
        insertStoreItem(db, "And", "Frankrig", "berberiandebryst frankrig", false, true, 0.3)

// Appelsin
        insertStoreItem(db, "Appelsin", "Spanien", "appelsiner oko spanien", true, true, 1.50)
        insertStoreItem(db, "Appelsin", "Spanien", "appelsiner 2 kg", false, true, 2.0)
        insertStoreItem(db, "Appelsin", "Spanien", "appelsiner spanien", false, false, 1 * 0.160)
        insertStoreItem(db, "Appelsin", "Egypten", "appelsiner egypten", false, false, 1 * 0.160)
        insertStoreItem(db, "Appelsin", "Sydafrika", "appelsiner sydafrika", false, false, 1 * 0.160)

// Artiskok
        insertStoreItem(db, "Artiskok", "Frankrig", "artiskok frankrig", false, false, 1 * 0.150)
        insertStoreItem(db, "Artiskok", "Spanien", "artiskok spanien", false, false, 1 * 0.150)

// Asparges
        insertStoreItem(db, "Asparges", "Italien", "gronne asparges i bundt italien", false, true, 0.5)
        insertStoreItem(db, "Asparges", "Spanien", "gronne asparges i bundt spanien", false, true, 0.5)

// Aubergine
        insertStoreItem(db, "Aubergine", "Holland", "aubergine holland", false, false, 1 * 0.250)
        insertStoreItem(db, "Aubergine", "Spanien", "aubergine oko spanien", true, true, 1 * 0.250)

// Avocado
        insertStoreItem(db, "Avocado", "Chile", "spisemodne avocadoer chile", false, true, 6 * 0.150)
        insertStoreItem(db, "Avocado", "Chile", "spisemodne avocadoer i bakke oko chile", true, true, 3 * 0.150)
        insertStoreItem(db, "Avocado", "Colombia", "spisemodne avocadoer colombia", false, true, 6 * 0.150)
        insertStoreItem(db, "Avocado", "Colombia", "spisemodne avocadoer i bakke oko colombia", true, true, 3 * 0.150)
        insertStoreItem(db, "Avocado", "Israel", "spisemodne avocadoer i bakke oko israel", true, true, 3 * 0.150)
        insertStoreItem(db, "Avocado", "Mexico", "spisemodne avocadoer mexico", false, true, 6 * 0.150)

// Banan
        insertStoreItem(db, "Banan", "Ecuador", "bananer 4 pak oko ecuador", true, true, 4 * 0.110)

// Basilikum
        insertStoreItem(db, "Basilikum", "Danmark", "basilikum danmark", false, true, 1 * 0.100)
        insertStoreItem(db, "Basilikum", "Danmark", "basilikum oko danmark", true, true, 1 * 0.100)

// Bladselleri
        insertStoreItem(db, "Bladselleri", "Danmark", "bladselleri i bundt danmark", false, true, 1 * 0.3)
        insertStoreItem(db, "Bladselleri", "Italien", "bladselleri i bundt italien", false, true, 1 * 0.3)
        insertStoreItem(db, "Bladselleri", "Italien", "bladselleri i bundt oko italien", true, true, 0.3)
        insertStoreItem(db, "Bladselleri", "Spanien", "bladselleri i bundt oko spanien", true, true, 0.3)
        insertStoreItem(db, "Bladselleri", "Spanien", "bladselleri i bundt spanien", false, true, 1 * 0.3)

// Blomkål
        insertStoreItem(db, "Blomkål", "Italien", "blomkal italien", false, false, 1 * 0.450)
        insertStoreItem(db, "Blomkål", "Italien", "blomkal oko italien", true, true, 1 * 0.450)
        insertStoreItem(db, "Blomkål", "Spanien", "blomkal oko spanien", true, true, 1 * 0.450)
        insertStoreItem(db, "Blomkål", "Spanien", "blomkal spanien", false, false, 1 * 0.450)

// Blomme
        insertStoreItem(db, "Blomme", "Chile", "blommer chile", false, true, 0.5)
        insertStoreItem(db, "Blomme", "Sydafrika", "blommer sydafrika", false, true, 0.5)

// Blåbær
        insertStoreItem(db, "Blåbær", "Argentina", "blaber oko argentina", true, true, 0.125)
        insertStoreItem(db, "Blåbær", "Chile", "blaber chile", false, true, 0.125)
        insertStoreItem(db, "Blåbær", "Chile", "blaber oko chile", true, true, 0.125)
        insertStoreItem(db, "Blåbær", "Peru", "blaber oko peru", true, true, 0.125)
        insertStoreItem(db, "Blåbær", "Peru", "blaber peru", false, true, 0.125)
        insertStoreItem(db, "Blåbær", "Polen", "blaber oko polen", true, true, 0.125)
        insertStoreItem(db, "Blåbær", "Polen", "blaber polen", false, true, 0.3)
        insertStoreItem(db, "Blåbær", "Sydafrika", "blaber sydafrika", false, true, 0.125)

// Broccoli
        insertStoreItem(db, "Broccoli", "Italien", "broccoli italien", false, false, 0.5)
        insertStoreItem(db, "Broccoli", "Italien", "broccoli oko italien", true, true, 0.35)
        insertStoreItem(db, "Broccoli", "Spanien", "broccoli oko spanien", true, true, 0.35)
        insertStoreItem(db, "Broccoli", "Spanien", "broccoli spanien", false, false, 0.5)

// Brombær
        insertStoreItem(db, "Brombær", "Belgien", "bromber belgien", false, true, 0.125)
        insertStoreItem(db, "Brombær", "Holland", "bromber holland", false, true, 0.125)
        insertStoreItem(db, "Brombær", "Marokko", "bromber marokko", false, true, 0.125)
        insertStoreItem(db, "Brombær", "Mexico", "bromber mexico", false, true, 0.125)
        insertStoreItem(db, "Brombær", "Portugal", "bromber portugal", false, true, 0.125)

// Bønnespirer
        insertStoreItem(db, "Bønnespirer", "Danmark", "babyspirer oko", true, true, 0.15)
        insertStoreItem(db, "Bønnespirer", "Danmark", "bonnespirer oko", false, true, 0.15)

// Champignon
        insertStoreItem(db, "Champignon", "Danmark", "brune champignon oko danmark", true, true, 0.2)
        insertStoreItem(db, "Champignon", "Polen", "hvide champignon polen", false, true, 0.5)

// Chili
        insertStoreItem(db, "Chili", "Holland", "chilimix oko holland", true, true, 0.07)
        insertStoreItem(db, "Chili", "Holland", "habanero chili holland", false, true, 0.03)
        insertStoreItem(db, "Chili", "Mozambique", "chilimix oko mozambique", true, true, 0.07)
        insertStoreItem(db, "Chili", "Mozambique", "habanero chili mozambique", false, true, 0.03)
        insertStoreItem(db, "Chili", "Spanien", "chilimix oko spanien", true, true, 0.07)
        insertStoreItem(db, "Chili", "Spanien", "habanero chili spanien", false, true, 0.03)

// Citron
        insertStoreItem(db, "Citron", "Italien", "citroner oko italien", true, true, 2 * 0.085)
        insertStoreItem(db, "Citron", "Italien", "citroner ubehandlede italien", false, true, 2 * 0.085)
        insertStoreItem(db, "Citron", "Spanien", "citroner oko spanien", true, true, 2 * 0.085)
        insertStoreItem(db, "Citron", "Spanien", "citroner ubehandlede spanien", false, true, 2 * 0.085)

// Dild
        insertStoreItem(db, "Dild", "Danmark", "dild danmark", false, true, 1 * 0.020)
        insertStoreItem(db, "Dild", "Holland", "dild holland", false, true, 1 * 0.020)
        insertStoreItem(db, "Dild", "Italien", "dild italien", false, true, 1 * 0.020)
        insertStoreItem(db, "Dild", "Kenya", "dild kenya", false, true, 1 * 0.020)
        insertStoreItem(db, "Dild", "Spanien", "dild spanien", false, true, 1 * 0.020)

// Fennikel
        insertStoreItem(db, "Fennikel", "Danmark", "fennikel oko danmark", true, true, 1 * 0.200)
        insertStoreItem(db, "Fennikel", "Italien", "fennikel oko italien", true, true, 1 * 0.200)

// Forårsløg
        insertStoreItem(db, "Forårsløg", "Egypten", "forarslog i bundt egypten", false, false, 1 * 0.100)
        insertStoreItem(db, "Forårsløg", "Marokko", "forarslog i bundt marokko", false, false, 1 * 0.100)
        insertStoreItem(db, "Forårsløg", "Senegal", "forarslog i bundt senegal", false, false, 1 * 0.100)
        insertStoreItem(db, "Forårsløg", "Egypten", "forarslog i bundt egypten", true, true, 1 * 0.100)
        insertStoreItem(db, "Forårsløg", "Marokko", "forarslog i bundt marokko", true, true, 1 * 0.100)
        insertStoreItem(db, "Forårsløg", "Senegal", "forarslog i bundt senegal", true, true, 1 * 0.100)

// Grapefrugt
        insertStoreItem(db, "Grapefrugt", "Italien", "grapefrugter italien", false, true, 2.0)
        insertStoreItem(db, "Grapefrugt", "Spanien", "grapefrugter spanien", false, true, 2.0)
        insertStoreItem(db, "Grapefrugt", "Spanien", "rod grapefrugt oko spanien", false, false, 1 * 0.350)

// Grisefilet
        insertStoreItem(db, "Grisefilet", "Danmark", "kotelet m. ben oko danmark", true, true, 0.25)
        insertStoreItem(db, "Grisefilet", "Danmark", "koteletter danmark", false, true, 0.40)

// Grisekød, hakket
        insertStoreItem(db, "Grisekød, hakket", "Danmark", "hakket grisekod 8-12% fedt friland oko danmark", true, true, 0.008)
        insertStoreItem(db, "Grisekød, hakket", "Danmark", "hakket grisekod 8-12% fedt", false, true, 0.008)

// Grisekød, nakkefilet
        insertStoreItem(db, "Grisekød, nakkefilet", "Danmark", "nakkefilet danmark", false, true, 1.50)
        insertStoreItem(db, "Grisekød, nakkefilet", "Danmark", "nakkekoteletter oko danmark", true, true, 0.27)

// Grisekød, svinekam med svær
        insertStoreItem(db, "Grisekød, svinekam med svær", "Danmark", "fleskesteg krogmodnet danmark", false, true, 1.1)
        insertStoreItem(db, "Grisekød, svinekam med svær", "Danmark", "fleskesteg oko danmark", true, true, 1.0)

// Græskar
        insertStoreItem(db, "Græskar", "Spanien", "butternut squash oko spanien", true, false, 1 * 1.000)
        insertStoreItem(db, "Græskar", "Sydafrika", "hokkaido greskar oko sydafrika", true, false, 1 * 1.000)

// Grønkål
        insertStoreItem(db, "Grønkål", "Danmark", "baby gronkal", false, true, 0.075)
        insertStoreItem(db, "Grønkål", "Danmark", "oko gronkal 250g", true, true, 0.250)
        insertStoreItem(db, "Grønkål", "Danmark", "snittet gronkal", false, true, 0.250)

// Grøntsagsbøffer
        insertStoreItem(db, "Grøntsagsbøffer", "Danmark", "plantebaserede boffer danmark", false, true, 0.24)

// Gulerod
        insertStoreItem(db, "Gulerod", "Danmark", "gulerodder oko danmark", true, true, 0.5)
        insertStoreItem(db, "Gulerod", "Danmark", "snackgulerodder danmark", false, true, 0.5)
        insertStoreItem(db, "Gulerod", "Italien", "gulerodder m. top italien", false, false, 1 * 0.065)
        insertStoreItem(db, "Gulerod", "Danmark", "gulerodder hands.", false, true, 1.0)

/// Hakket kylling
        insertStoreItem(db, "Hakket kylling", "Danmark", "hakket kyllingekod 3-7% fedt danmark", false, true, 0.003)
        insertStoreItem(db, "Hakket kylling", "Danmark", "hakket kyllingekod 3-6% fedt fritgående oko", true, true, 0.003)

// Hakket lammekød
        insertStoreItem(db, "Hakket lammekød", "Tyskland", "hakket lammekod 10-12% fedt tyskland", false, true, 0.01)

// Hindbær
        insertStoreItem(db, "Hindbær", "Marokko", "hindber marokko", false, true, 0.125)
        insertStoreItem(db, "Hindbær", "Portugal", "hindber portugal", false, true, 0.125)
        insertStoreItem(db, "Hindbær", "Spanien", "hindber oko spanien", true, true, 0.2)
        insertStoreItem(db, "Hindbær", "Spanien", "hindber spanien", false, true, 0.25)

// Honningmelon
        insertStoreItem(db, "Honningmelon", "Brasilien", "dinomelon brasilien", false, false, 1 * 0.8)
        insertStoreItem(db, "Honningmelon", "Costa Rica", "gul honningmelon costa rica", false, false, 1 * 0.8)

// Hvidkål
        insertStoreItem(db, "Hvidkål", "Danmark", "hvidkal danmark", false, false, 1 * 1.0)

// Hvidløg
        insertStoreItem(db, "Hvidløg", "Kina", "hvidlog kina", false, true, 0.25)
        insertStoreItem(db, "Hvidløg", "Spanien", "hvidlog spanien", false, true, 0.18)

// Høne
        insertStoreItem(db, "Høne", "Polen", "suppehone polen", false, true, 0.8)

// Icebergsalat
        insertStoreItem(db, "Icebergsalat", "Spanien", "icebergsalat oko spanien", true, true, 1 * 0.4)
        insertStoreItem(db, "Icebergsalat", "Spanien", "icebergsalat spanien", false, true, 1 * 0.4)

// Ingefær, rod
        insertStoreItem(db, "Ingefær, rod", "Kina", "ingefer kina", false, true, 0.2)
        insertStoreItem(db, "Ingefær, rod", "Peru", "ingefer oko peru", true, true, 0.2)
        insertStoreItem(db, "Ingefær, rod", "Peru", "ingefer peru", false, true, 0.2)

// Jordbær
        insertStoreItem(db, "Jordbær", "Belgien", "jordber belgien", false, true, 0.4)
        insertStoreItem(db, "Jordbær", "Egypten", "jordber egypten", false, true, 0.4)
        insertStoreItem(db, "Jordbær", "Holland", "jordber holland", false, true, 0.4)
        insertStoreItem(db, "Jordbær", "Marokko", "jordber marokko", false, true, 0.4)
        insertStoreItem(db, "Jordbær", "Spanien", "jordber oko spanien", true, true, 0.225)
        insertStoreItem(db, "Jordbær", "Spanien", "jordber spanien", false, true, 0.4)

// Kalkun
        insertStoreItem(db, "Kalkun", "Tyskland", "kalkunbrystfilet tyskland", false, true, 0.8)

// Kalv og flæsk, hakket
        insertStoreItem(db, "Kalv og flæsk, hakket", "Danmark", "hakket grise- og oksekod 8-12% fedt oko ", false, true, 0.40)
        insertStoreItem(db, "Kalv og flæsk, hakket", "Danmark", "hakket grise- og kalvekod 14-18% fedt danmark", false, true, 0.60)

// Kalvekød
        insertStoreItem(db, "Kalvekød", "Danmark", "kalvesteg m. chili danmark", false, true, 0.30)
        insertStoreItem(db, "Kalvekød", "Holland", "kalvemorbrad 1-1,7 kg holland", false, true, 1.0)
        insertStoreItem(db, "Kalvekød", "Tyskland", "kalvemorbrad 1-1,7 kg tyskland", false, true, 1.0)

// Karse
        insertStoreItem(db, "Karse", "Danmark", "brondkarse oko danmark", true, true, 1 * 0.1)
        insertStoreItem(db, "Karse", "Danmark", "karse, lille bakke", false, true, 1 * 0.1)

// Kartoffel
        insertStoreItem(db, "Kartoffel", "Danmark", "kartofler oko danmark", true, true, 1.0)
        insertStoreItem(db, "Kartoffel", "Danmark", "vildmose kartofler danmark", false, true, 2.0)
        insertStoreItem(db, "Kartoffel", "Egypten", "kartofler oko egypten", true, true, 1.0)
        insertStoreItem(db, "Kartoffel", "Egypten", "nemme kartofler egypten", false, true, 0.8)
        insertStoreItem(db, "Kartoffel", "England", "bagekartofler oko england", true, true, 1.20)
        insertStoreItem(db, "Kartoffel", "Frankrig", "babykartofler frankrig", false, true, 0.7)
        insertStoreItem(db, "Kartoffel", "Italien", "bagekartofler oko italien", true, true, 1.20)
        insertStoreItem(db, "Kartoffel", "Spanien", "babykartofler spanien", false, true, 0.7)

// Kidney bønner
        insertStoreItem(db, "Kidney bønner", "Kina", "kidneybonner oko kina", true, true, 0.15)
        insertStoreItem(db, "Kidney bønner", "Kina", "kidneybonner kina", false, true, 0.240)

// Kikærter
        insertStoreItem(db, "Kikærter, tørrede", "Tyrkiet", "kikerter oko tyrkiet", true, true, 0.41)
        insertStoreItem(db, "Kikærter, tørrede", "Tyrkiet", "kikerter tyrkiet", false, true, 0.24)
        insertStoreItem(db, "Kikærter, tørrede", "Tyrkiet", "levevis kikerter", true, true, 0.400)

// Kiwi
        insertStoreItem(db, "Kiwi", "Chile", "kiwi oko chile", true, true, 0.5)
        insertStoreItem(db, "Kiwi", "Italien", "kiwi oko italien", true, true, 0.5)

// Knoldselleri
        insertStoreItem(db, "Knoldselleri", "Danmark", "knoldselleri danmark", false, false, 1 * 0.8)
        insertStoreItem(db, "Knoldselleri", "Danmark", "knoldselleri oko danmark", true, true, 1 * 0.8)

// Kylling, bryst
        insertStoreItem(db, "Kylling, bryst", "Danmark", "kyllingestrimler danmark", false, true, 0.3)
        insertStoreItem(db, "Kylling, bryst", "Danmark", "kyllingebrystfilet fritgaende oko", true, true, 0.225)

// Kylling, hel
        insertStoreItem(db, "Kylling, hel", "Danmark", "hel kylling fritgaende danmark", false, true, 1.20)
        insertStoreItem(db, "Kylling, hel", "Danmark", "hel kylling fritgaende oko danmark", false, true, 1.20)
        insertStoreItem(db, "Kylling, hel", "Frankrig", "hel kylling friland frankrig", false, true, 1.30)

// Kylling, lår
        insertStoreItem(db, "Kylling, lår", "Danmark", "kyllingelarmix danmark", false, true, 0.6)
        insertStoreItem(db, "Kylling, lår", "Danmark", "kyllingeoverlar oko danmark", true, true, 1.0)

// Lammekød
        insertStoreItem(db, "Lammekød", "New Zealand", "lammeculotte new zealand", false, true, 0.275)

// Lammekølle
        insertStoreItem(db, "Lammekølle", "Chile", "lammekolle chile", false, true, 1.90)

// Lime
        insertStoreItem(db, "Lime", "Brasilien", "limefrugter oko brasilien", true, true, 4 * 0.110)
        insertStoreItem(db, "Lime", "Brasilien", "limefrugter, ubehandlede brasilien", false, true, 6 * 0.110)
        insertStoreItem(db, "Lime", "Colombia", "limefrugter oko colombia", true, true, 4 * 0.110)
        insertStoreItem(db, "Lime", "Colombia", "limefrugter, ubehandlede colombia", false, true, 6 * 0.110)
        insertStoreItem(db, "Lime", "Peru", "limefrugter oko peru", true, true, 4 * 0.110)
        insertStoreItem(db, "Lime", "Peru", "limefrugter, ubehandlede peru", false, true, 6 * 0.110)
        insertStoreItem(db, "Lime", "Vietnam", "limefrugter oko vietnam", true, true, 4 * 0.110)
        insertStoreItem(db, "Lime", "Vietnam", "limefrugter, ubehandlede vietnam", false, true, 6 * 0.110)

// Linser, tørrede
        insertStoreItem(db, "Linser, tørrede", "Tyrkiet", "gronne linser tyrkiet", true, true, 0.4)
        insertStoreItem(db, "Linser, tørrede", "Tyrkiet", "belugalinser oko tyrkiet", true, true, 0.4)

// Løg
        insertStoreItem(db, "Løg", "Danmark", "skalottelog oko danmark", true, true, 0.2)
        insertStoreItem(db, "Løg", "Danmark", "log danmark", false, true, 1.0)
        insertStoreItem(db, "Løg", "Danmark", "rodlog", false, true, 1.0)
        insertStoreItem(db, "Løg", "Egypten", "log oko egypten", true, true, 0.75)
        insertStoreItem(db, "Løg", "Holland", "blandede log oko holland", true, true, 0.4)

// Majskolbe
        insertStoreItem(db, "Majskolbe", "Holland", "majskolber oko holland", true, true, 0.4)

// Mango
        insertStoreItem(db, "Mango", "Brasilien", "mango brasilien", false, false, 1 * 0.3)
        insertStoreItem(db, "Mango", "Brasilien", "mango, spisemodne brasilien", false, true, 2 * 0.3)
        insertStoreItem(db, "Mango", "Den Dominikanske Republik", "mango den dominikanske republik", false, false, 1 * 0.3)
        insertStoreItem(db, "Mango", "Den Dominikanske Republik", "mango, spisemodne den dominikanske republik", false, true, 2 * 0.3)
        insertStoreItem(db, "Mango", "Peru", "mango i tern oko peru", true, true, 0.25)
        insertStoreItem(db, "Mango", "Peru", "mango peru", false, false, 1 * 0.3)
        insertStoreItem(db, "Mango", "Peru", "mango, spisemodne peru", false, true, 2 * 0.3)

// Medisterpølse
        insertStoreItem(db, "Medisterpølse", "Danmark", "baconmedister danmark", false, true, 0.75)
        insertStoreItem(db, "Medisterpølse", "Danmark", "medister oko danmark", true, true, 0.35)

// Okseculotte
        insertStoreItem(db, "Okseculotte", "Danmark", "okseculotte danmark", false, true, 1.30)
        insertStoreItem(db, "Okseculotte", "Holland", "okseculotte holland", false, true, 1.30)
        insertStoreItem(db, "Okseculotte", "Tyskland", "okseculotte tyskland", false, true, 1.30)

// Okseinderlår
        insertStoreItem(db, "Okseinderlår", "Danmark", "okseinderlar danmark", false, true, 1.0)
        insertStoreItem(db, "Okseinderlår", "Holland", "okseinderlar holland", false, true, 1.0)
        insertStoreItem(db, "Okseinderlår", "Tyskland", "okseinderlar tyskland", false, true, 1.0)

// Oksekød, hakket
        insertStoreItem(db, "Oksekød, hakket", "Danmark", "mh okse 7%", false, true, 0.004)
        insertStoreItem(db, "Oksekød, hakket", "Frankrig", "hakket oksekod 8-12% fedt oko frankrig", true, true, 0.008)
        insertStoreItem(db, "Oksekød, hakket", "Holland", "hakket oksekod 4-7% fedt holland", false, true, 0.004)
        insertStoreItem(db, "Oksekød, hakket", "Tyskland", "hakket oksekod 4-7% fedt tyskland", false, true, 0.004)
        insertStoreItem(db, "Oksekød, hakket", "Tyskland", "hakket oksekod 8-12% fedt oko tyskland", true, true, 0.008)
        insertStoreItem(db, "Oksekød, hakket", "Østrig", "hakket oksekod 8-12% fedt oko ostrig", true, true, 0.008)

// Oksemørbrad
        insertStoreItem(db, "Oksemørbrad", "Danmark", "oksemorbrad danmark", false, true, 1.5)
        insertStoreItem(db, "Oksemørbrad", "Danmark", "oksemorbrad oko danmark", true, true, 0.5)
        insertStoreItem(db, "Oksemørbrad", "Holland", "oksemorbrad holland", false, true, 1.5)
        insertStoreItem(db, "Oksemørbrad", "Tyskland", "oksemorbrad tyskland", false, true, 1.5)

// Peberfrugt
        insertStoreItem(db, "Peberfrugt", "Holland", "california peberfrugter oko holland", true, true, 0.3)
        insertStoreItem(db, "Peberfrugt", "Holland", "lose rød peberfrugter holland", false, false, 1 * 0.180)
        insertStoreItem(db, "Peberfrugt", "Holland", "sod rød snackpeber kernefri holland", false, true, 0.3)
        insertStoreItem(db, "Peberfrugt", "Israel", "california peberfrugter oko israel", true, true, 0.3)
        insertStoreItem(db, "Peberfrugt", "Israel", "rod snackpeber oko israel", true, true, 0.18)
        insertStoreItem(db, "Peberfrugt", "Marokko", "sod rød snackpeber kernefri marokko", false, true, 0.3)
        insertStoreItem(db, "Peberfrugt", "Spanien", "california peberfrugter oko spanien", true, true, 0.3)
        insertStoreItem(db, "Peberfrugt", "Spanien", "lose rød peberfrugter spanien", false, false, 1 * 0.180)
        insertStoreItem(db, "Peberfrugt", "Spanien", "sod rød snackpeber kernefri spanien", false, true, 0.3)
        insertStoreItem(db, "Peberfrugt", "Spanien", "oko rod peber", true, true, 0.180)

// Persille
        insertStoreItem(db, "Persille", "Danmark", "bredbladet persille oko danmark", true, true, 0.05)
        insertStoreItem(db, "Persille", "Danmark", "kruspersille danmark", false, true, 1 * 0.050)
        insertStoreItem(db, "Persille", "Italien", "bredbladet persille oko italien", true, true, 0.05)
        insertStoreItem(db, "Persille", "Spanien", "bredbladet persille oko spanien", true, true, 0.05)
        insertStoreItem(db, "Persille", "Spanien", "hakket persille spanien", false, true, 0.15)

// Persillerod
        insertStoreItem(db, "Persillerod", "Danmark", "persillerod oko danmark", true, true, 0.5)

// Porre
        insertStoreItem(db, "Porre", "Danmark", "porrer i bundt danmark", false, true, 3 * 0.15)

// Portobello
        insertStoreItem(db, "Portobello", "Danmark", "portobello svampe oko danmark", true, true, 0.25)

// Purløg
        insertStoreItem(db, "Purløg", "Danmark", "purlog danmark", false, true, 1 * 0.1)

// Pære
        insertStoreItem(db, "Pære", "Holland", "perer holland", false, true, 6 * 0.15)
        insertStoreItem(db, "Pære", "Sydafrika", "perer sydafrika", false, true, 6 * 0.15)
        insertStoreItem(db, "Pære", "Italien", "perer oko italien", true, true, 0.5)
        insertStoreItem(db, "Pære", "Spanien", "perer oko spanien", true, true, 0.5)
        insertStoreItem(db, "Pære", "Holland", "perer oko holland", true, true, 0.5)
// Quinoa
        insertStoreItem(db, "Quinoa", "Peru", "sort quinoa oko peru", true, true, 0.35)

// Radise
        insertStoreItem(db, "Radise", "Holland", "radiser holland", false, true, 0.25)
        insertStoreItem(db, "Radise", "Marokko", "radiser oko marokko", true, true, 0.25)

// Rosenkål
        insertStoreItem(db, "Rosenkål", "Holland", "rosenkal holland", false, true, 0.35)
        insertStoreItem(db, "Rosenkål", "Marokko", "rosenkal marokko", false, true, 0.35)

// Rødbede
        insertStoreItem(db, "Rødbede", "Danmark", "rodbeder oko danmark", true, true, 1.0)

// Rødkål
        insertStoreItem(db, "Rødkål", "Holland", "rod spidskal oko holland", true, true, 1 * 1.0)
        insertStoreItem(db, "Rødkål", "Portugal", "rod spidskal oko portugal", true, true, 1 * 1.0)
        insertStoreItem(db, "Rødkål", "Spanien", "rod spidskal oko spanien", true, true, 1 * 1.0)

// Savoykål
        insertStoreItem(db, "Savoykål", "Italien", "savoykal oko italien", true, true, 1 * 1.0)

// Skinkeschnitzel
        insertStoreItem(db, "Skinkeschnitzel", "Danmark", "skinkeschnitzler danmark", false, true, 0.29)

// Sorte bønner
        insertStoreItem(db, "Sorte bønner", "Kina", "sorte bonner oko kina", true, true, 0.24)
        insertStoreItem(db, "Sorte bønner", "Kina", "sorte bonner kina", false, true, 0.24)

// Spidskål
        insertStoreItem(db, "Spidskål", "Danmark", "spidskal, rod", true, true, 1 * 0.7)
        insertStoreItem(db, "Spidskål", "Holland", "spidskal holland", false, true, 1 * 0.7)
        insertStoreItem(db, "Spidskål", "Holland", "spidskal oko holland", true, true, 1 * 0.7)
        insertStoreItem(db, "Spidskål", "Portugal", "spidskal oko portugal", true, true, 1 * 0.7)
        insertStoreItem(db, "Spidskål", "Portugal", "spidskal portugal", false, true, 1 * 0.7)
        insertStoreItem(db, "Spidskål", "Spanien", "spidskal oko spanien", true, true, 1 * 0.7)
        insertStoreItem(db, "Spidskål", "Spanien", "spidskal spanien", false, true, 1 * 0.7)

// Spinat
        insertStoreItem(db, "Spinat", "Danmark", "skyllet spinat oko danmark", true, true, 0.2)
        insertStoreItem(db, "Spinat", "Danmark", "spinat danmark", false, true, 0.25)
        insertStoreItem(db, "Spinat", "Spanien", "spinat spanien", false, true, 0.25)

// Squash
        insertStoreItem(db, "Squash", "Spanien", "squash oko spanien", true, false, 1 * 0.28)
        insertStoreItem(db, "Squash", "Spanien", "squash spanien", false, false, 1 * 0.28)


// Svinemørbrad
        insertStoreItem(db, "Svinemørbrad", "Danmark", "svinemorbrad danmark", false, true, 0.50)

        // Tofu
        insertStoreItem(db, "Tofu", "Bulgarien", "tofu", false, true, 0.2)
        insertStoreItem(db, "Tofu", "Bulgarien", "tofu oko", false, true, 0.45)

// Tomat
        insertStoreItem(db, "Tomat", "Danmark", "ida tomater danmark", false, true, 0.2)
        insertStoreItem(db, "Tomat", "Holland", "gusto tomater holland", false, true, 0.45)
        insertStoreItem(db, "Tomat", "Holland", "tomater oko holland", true, true, 0.5)
        insertStoreItem(db, "Tomat", "Marokko", "cherry blomme tomatmix marokko", false, true, 0.5)
        insertStoreItem(db, "Tomat", "Spanien", "tomater oko spanien", true, true, 0.5)
        insertStoreItem(db, "Tomat", "Marokko","lose tomater", false, false, 0.075)
        insertStoreItem(db, "Tomat", "Spanien","cocktailtomat", false, true, 0.500)
        insertStoreItem(db, "Tomat", "Spanien", "blommetomater", false, true, 0.500)

// Vandmelon
        insertStoreItem(db, "Vandmelon", "Brasilien", "vandmelon brasilien", false, false, 1 * 0.700)
        insertStoreItem(db, "Vandmelon", "Costa Rica", "vandmelon costa rica", false, false, 1 * 0.700)

// Vegansk fars
        insertStoreItem(db, "Vegansk fars", "Danmark", "plantebaseret fars hakket vegansk danmark", false, true, 0.35)

// Vindrue
        insertStoreItem(db, "Vindrue", "Brasilien", "rode druer brasilien", false, true, 0.5)
        insertStoreItem(db, "Vindrue", "Chile", "rode druer chile", false, true, 0.5)
        insertStoreItem(db, "Vindrue", "Indien", "rode druer indien", false, true, 0.5)
        insertStoreItem(db, "Vindrue", "Namibia", "rode druer namibia", false, true, 0.5)
        insertStoreItem(db, "Vindrue", "Peru", "rode druer peru", false, true, 0.5)
        insertStoreItem(db, "Vindrue", "Sydafrika", "rode druer sydafrika", false, true, 0.5)

// Æble
        insertStoreItem(db, "Æble", "Chile", "ebler chile", false, true, 1.0)
        insertStoreItem(db, "Æble", "Frankrig", "ebler frankrig", false, true, 1.0)
        insertStoreItem(db, "Æble", "Frankrig", "ebler oko frankrig", true, true, 4 * 0.150)
        insertStoreItem(db, "Æble", "Italien", "ebler oko italien", true, true, 4 * 0.150)
        insertStoreItem(db, "Æble", "Italien", "ebler, royal gala italien", false, true, 8 * 0.150)

// Ærter
        insertStoreItem(db, "Ærter", "Danmark", "erter oko danmark", true, true, 0.4)
        insertStoreItem(db, "Ærter", "Italien", "erter italien", false, true, 0.45)
        insertStoreItem(db, "Ærter", "Portugal", "erter portugal", false, true, 0.45)
        insertStoreItem(db, "Ærter", "Guatemala", "sukkererter guatemala", false, true, 0.125)
        insertStoreItem(db, "Ærter", "Kenya", "sukkererter kenya", false, true, 0.125)
        insertStoreItem(db, "Ærter", "Zimbabwe", "sukkererter zimbabwe", false, true, 0.125)
    }

    private fun insertStoreItem(db: SQLiteDatabase, productName: String, countryName: String, receiptText: String, organic: Boolean, packaged: Boolean, weight: Double, store: String = "Føtex"): Long {
        val contentValues = ContentValues()
        val productID = getProductID(db, productName)
        val countryID = getCountryID(db, countryName)

        contentValues.put("productID", productID)
        contentValues.put("countryID", countryID)
        contentValues.put("receiptText", receiptText)
        contentValues.put("organic", organic)
        contentValues.put("packaged", packaged)
        contentValues.put("weight", weight)
        contentValues.put("countryDefault", false)
        contentValues.put("weightDefault", false)
        contentValues.put("store", store)

        return db.insert("storeItem", null, contentValues)
    }

    private fun getCountryID(db: SQLiteDatabase, countryName: String): Long{
        val query = "SELECT ${CountryDao.COLUMN_ID} FROM ${CountryDao.TABLE} WHERE ${CountryDao.COLUMN_NAME} = '$countryName';"

        try {
            return select(query, db) {
                it.getLong(0)
            }!!

        } catch (e: Exception) {
            throw Exception("Missing: $countryName")
        }
    }

    private fun getProductID(db: SQLiteDatabase, productName: String): Long{
        val query = "SELECT ${ProductDao.COLUMN_ID} FROM ${ProductDao.TABLE} WHERE ${ProductDao.COLUMN_NAME} = '$productName';"

        try {
            return select(query, db) {
                it.getLong(0)
            }!!
        } catch (e: Exception) {
            throw Exception("Missing: $productName")
        }
    }

    private fun insertVariables(db: SQLiteDatabase) : Long{
        val contentValues = ContentValues()

        contentValues.put(VariablesDao.COLUMN_ID, 1)
        contentValues.put(VariablesDao.COLUMN_SCORE, 0)
        contentValues.put(VariablesDao.COLUMN_ENABLE_GAME, 1)

        return db.insert(VariablesDao.TABLE, null, contentValues)
    }

    fun insert(table: String, contentValues: ContentValues): Long {
        return writableDatabase.insert(table, null, contentValues)
    }

    fun update(table: String, contentValues: ContentValues, idName: String, id: String): Int {
        return writableDatabase.update(table, contentValues, "$idName = ?", arrayOf(id))
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

    fun <T> select(query: String, database: SQLiteDatabase? = null, producer: (cursor: Cursor) -> T): T? {
        val db = database ?: readableDatabase

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
