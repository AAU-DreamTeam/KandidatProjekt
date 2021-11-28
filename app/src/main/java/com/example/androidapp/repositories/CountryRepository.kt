package com.example.androidapp.repositories

import android.content.Context
import com.example.androidapp.data.models.Country
import com.example.androidapp.data.models.daos.CountryDao

class CountryRepository(context: Context) {
    private val countryDao = CountryDao(context)

    fun loadCountries(): List<String> {
        return countryDao.loadCountries()
    }
}