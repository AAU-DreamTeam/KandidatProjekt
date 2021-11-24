package com.example.androidapp.data.models

//id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, cultivation REAL NOT NULL, iluc REAL NOT NULL, processing REAL NOT NULL, packaging REAL NOT NULL, retail REAL NOT NULL, GHCultivated BOOLEAN NOT NULL CHECK(GHCultivated IN (0, 1))
class Product(_name: String, _cultivation: Double, _iluc: Double, _processing: Double, _packaging: Double, _retail: Double, _ghCultivated: Boolean) {
    val name = _name
    val cultivation = _cultivation
    val iluc = _iluc
    val processing = _processing
    val packaging = _packaging
    val retail = _retail
    val ghCultivated = _ghCultivated
}