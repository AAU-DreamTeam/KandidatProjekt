package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.models.Country
import com.example.androidapp.models.daos.CountryDao

class CountryRepository(context: Context) {
    private val countryDao = CountryDao(context)

    fun loadCountries(): List<Country> {
        return countryDao.loadCountries()
    }

    fun close(){
        countryDao.close()
    }
}