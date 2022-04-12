package androidapp.CO2Mad.repositories

import android.content.Context
import androidapp.CO2Mad.models.Country
import androidapp.CO2Mad.models.daos.CountryDao

class CountryRepository(context: Context) {
    private val countryDao = CountryDao(context)

    fun loadCountries(): List<Country> {
        return countryDao.loadCountries()
    }

    fun close(){
        countryDao.close()
    }
}