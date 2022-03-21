package com.example.androidapp.models.tools

import com.example.androidapp.models.StoreItem
import com.example.androidapp.models.daos.CountryDao
import com.example.androidapp.models.daos.ProductDao
import com.example.androidapp.models.daos.PurchaseDao
import com.example.androidapp.models.daos.StoreItemDao

class EmissionCalculator {
    companion object{
        private val GH_PENALTY = 8.0
        private val ORGANIC_PENALTY = 1.21
        private val NO_PENALTY = 1.0
        private val NO_EMISSION = 0.0

        fun calcEmission(storeItem: StoreItem): Double {
            val packagingEmission = if (storeItem.packaged) storeItem.product.packaging else NO_EMISSION
            val organicPenalty = if (storeItem.organic) ORGANIC_PENALTY else NO_PENALTY
            val ghPenalty = if (storeItem.country.ghPenalty && storeItem.product.ghCultivated) GH_PENALTY else NO_PENALTY

            return storeItem.product.iluc +
                    storeItem.product.processing +
                    storeItem.product.retail +
                    storeItem.product.cultivation * organicPenalty * ghPenalty +
                    packagingEmission +
                    storeItem.country.transportEmission
        }

        fun sqlEmissionPerKgFormula(): String {
            val packagingEmission =
                    "CASE WHEN ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_PACKAGED} = 1 THEN ${ProductDao.TABLE}.${ProductDao.COLUMN_PACKAGING} " +
                         "ELSE $NO_EMISSION " +
                    "END"
            val organicPenalty =
                    "CASE WHEN ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_ORGANIC} = 1 THEN $ORGANIC_PENALTY " +
                         "ELSE $NO_PENALTY " +
                    "END"
            val ghPenalty =
                    "CASE WHEN ${ProductDao.TABLE}.${ProductDao.COLUMN_GHCULTIVATED} = 1 AND ${CountryDao.TABLE}.${CountryDao.COLUMN_GHPENALTY} = 1 THEN $GH_PENALTY " +
                         "ELSE $NO_PENALTY " +
                    "END"
            return "${ProductDao.TABLE}.${ProductDao.COLUMN_ILUC} + " +
                    "${ProductDao.TABLE}.${ProductDao.COLUMN_PROCESSING} + " +
                    "${ProductDao.TABLE}.${ProductDao.COLUMN_RETAIL} + " +
                    "${ProductDao.TABLE}.${ProductDao.COLUMN_CULTIVATION} * $organicPenalty * $ghPenalty + " +
                    "$packagingEmission + " +
                    "${CountryDao.TABLE}.${CountryDao.COLUMN_TRANSPORT_EMISSION}"
        }

        fun sqlEmissionPerPurchaseFormula(): String {
            return "(${sqlEmissionPerKgFormula()}) * ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_WEIGHT} * ${PurchaseDao.TABLE}.${PurchaseDao.COLUMN_QUANTITY}"
        }
    }
}