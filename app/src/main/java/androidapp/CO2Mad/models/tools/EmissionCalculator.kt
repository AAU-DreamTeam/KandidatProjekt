package androidapp.CO2Mad.models.tools

import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.models.daos.CountryDao
import androidapp.CO2Mad.models.daos.ProductDao
import androidapp.CO2Mad.models.daos.PurchaseDao
import androidapp.CO2Mad.models.daos.StoreItemDao
import androidapp.CO2Mad.models.enums.RATING

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
            return "(${ProductDao.TABLE}.${ProductDao.COLUMN_ILUC} + " +
                    "${ProductDao.TABLE}.${ProductDao.COLUMN_PROCESSING} + " +
                    "${ProductDao.TABLE}.${ProductDao.COLUMN_RETAIL} + " +
                    "${ProductDao.TABLE}.${ProductDao.COLUMN_CULTIVATION} * $organicPenalty * $ghPenalty + " +
                    "$packagingEmission + " +
                    "${CountryDao.TABLE}.${CountryDao.COLUMN_TRANSPORT_EMISSION})"
        }

        fun sqlEmissionPerPurchaseFormula(): String {
            return "((${sqlEmissionPerKgFormula()}) * ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_WEIGHT} * ${PurchaseDao.TABLE}.${PurchaseDao.COLUMN_QUANTITY})"
        }

        fun test(): String {
            return "(SELECT ${sqlRatingFormular()} AS rating, id FROM ${sqlRatingTable()})"
        }

        fun sqlRatingTable(): String {
            val ratingVariables = "ratingValues"
            val productID = "prodID"
            val minimum = "minimum"
            val maximum = "maximum"
            val range = "(maximum - minimum)"
            val min = "MIN(${sqlEmissionPerKgFormula()})"
            val max = "MAX(${sqlEmissionPerKgFormula()})"

            return "(SELECT ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_ID}, ${sqlEmissionPerKgFormula()} AS emission, $range AS range, $minimum " +
                    "FROM ${StoreItemDao.TABLE} " +
                    "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                    "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                    "INNER JOIN (SELECT ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} AS $productID, " +
                                       "$min AS $minimum, " +
                                       "$max AS $maximum " +
                                "FROM ${StoreItemDao.TABLE} " +
                                "INNER JOIN ${ProductDao.TABLE} ON ${StoreItemDao.COLUMN_PRODUCT_ID} = ${ProductDao.TABLE}.${ProductDao.COLUMN_ID} " +
                                "INNER JOIN ${CountryDao.TABLE} ON ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_COUNTRY_ID} = ${CountryDao.TABLE}.${CountryDao.COLUMN_ID} " +
                                "GROUP BY $productID) $ratingVariables " +
                    "ON ${StoreItemDao.TABLE}.${StoreItemDao.COLUMN_PRODUCT_ID} = $ratingVariables.$productID)"
        }

        fun sqlRatingFormular(): String{
            return "(CASE " +
                    "WHEN emission >= minimum + range * 0.8 THEN ${RATING.VERY_BAD.ordinal} " +
                    "WHEN emission >= minimum + range * 0.6 THEN ${RATING.BAD.ordinal} " +
                    "WHEN emission >= minimum + range * 0.4 THEN ${RATING.OK.ordinal} " +
                    "WHEN emission >= minimum + range * 0.2 THEN ${RATING.GOOD.ordinal} " +
                    "ELSE ${RATING.VERY_GOOD.ordinal} " +
                    "END)"
        }
    }
}