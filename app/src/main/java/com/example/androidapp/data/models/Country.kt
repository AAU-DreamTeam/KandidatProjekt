package com.example.androidapp.data.models
//id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, transportEmission REAL NOT NULL, GHPenalty BOOLEAN NOT NULL CHECK(GHPenalty IN (0, 1))
class Country(_name: String, _transportEmission: Double, _ghPenalty: Boolean) {
    val name = _name
    val transportEmission = _transportEmission
    val ghPenalty = _ghPenalty
}