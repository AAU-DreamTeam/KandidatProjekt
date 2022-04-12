package androidapp.CO2Mad.models.tools

import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.models.daos.CountryDao
import androidapp.CO2Mad.models.daos.ProductDao
import androidapp.CO2Mad.models.daos.PurchaseDao
import androidapp.CO2Mad.models.daos.StoreItemDao
import androidapp.CO2Mad.models.enums.PRODUCT_CATEGORY
import androidapp.CO2Mad.models.enums.RATING

class EmissionCalculator {
    companion object {
        private val GH_PENALTY = 8.0
        private val ORGANIC_PENALTY = 1.21
        private val NO_PENALTY = 1.0
        private val NO_EMISSION = 0.0

        fun calcEmission(storeItem: StoreItem): Double {
            val packagingEmission =
                if (storeItem.packaged) storeItem.product.packaging else NO_EMISSION
            val organicPenalty = if (storeItem.organic) ORGANIC_PENALTY else NO_PENALTY
            val ghPenalty =
                if (storeItem.country.ghPenalty && storeItem.product.ghCultivated) GH_PENALTY else NO_PENALTY

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

        fun rate(storeItem: StoreItem): RATING {
            if (storeItem.product.productCategory == PRODUCT_CATEGORY.VEGETABLES) {
                return if (storeItem.altEmissions!!.isEmpty()) {
                    RATING.VERY_GOOD
                } else {
                    val emission = storeItem.emissionPerKg
                    val difference = (emission - storeItem.altEmissions!!.first().second) / emission
                    when {
                        difference > 0.8 -> RATING.VERY_BAD
                        difference > 0.6 -> RATING.BAD
                        difference > 0.4 -> RATING.OK
                        difference > 0.2 -> RATING.GOOD
                        else -> RATING.VERY_GOOD
                    }
                }
            } else {
                val emission = storeItem.emissionPerKg

                return when {
                    emission >= 47.8 -> RATING.VERY_BAD
                    emission >= 30.5 -> RATING.BAD
                    emission >= 15.5 -> RATING.OK
                    emission >= 3.1 -> RATING.GOOD
                    else -> RATING.VERY_GOOD
                }
            }
        }

        fun rateAlternative(original: StoreItem, alternative: StoreItem): RATING {
            val emissionOriginal = original.emissionPerKg
            val originalRatingOrdinal = original.rating!!.ordinal
            val difference = (emissionOriginal - alternative.emissionPerKg) / emissionOriginal

            return when {
                difference > 0.8 -> RATING.VERY_GOOD
                difference > 0.6 -> RATING.values()[originalRatingOrdinal + 3]
                difference > 0.4 -> RATING.values()[originalRatingOrdinal + 2]
                difference > 0.2 -> RATING.values()[originalRatingOrdinal + 1]
                else -> original.rating!!
            }
        }
    }
}