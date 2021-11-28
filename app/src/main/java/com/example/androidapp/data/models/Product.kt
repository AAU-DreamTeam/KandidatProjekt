package com.example.androidapp.data.models

//id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, cultivation REAL NOT NULL, iluc REAL NOT NULL, processing REAL NOT NULL, packaging REAL NOT NULL, retail REAL NOT NULL, GHCultivated BOOLEAN NOT NULL CHECK(GHCultivated IN (0, 1))
class Product(val id: Int,
              val name: String,
              val cultivation: Double,
              val iluc: Double,
              val processing: Double,
              val packaging: Double,
              val retail: Double,
              val ghCultivated: Boolean){
    constructor(): this(0, "", 0.0, 0.0, 0.0, 0.0, 0.0, false)
}