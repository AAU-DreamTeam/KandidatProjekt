package com.example.androidapp.data.models.daos

import android.content.Context
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Country
import com.example.androidapp.data.models.Product
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.StoreItem
import com.example.androidapp.viewmodels.MONTH

class PurchaseDao(context: Context) {

    val dbManager = DBManager(context)

    fun getAllFromYearAndMonth(year: String, month: MONTH): MutableList<Purchase> {
        val query =
                "SELECT purchase.id, " +                //0
                       "storeItem.id, " +               //1
                       "storeItem.receiptText, " +      //2
                       "storeItem.organic, " +          //3
                       "storeItem.packaged, " +         //4
                       "storeItem.weight, " +           //5
                       "storeItem.store, " +            //6
                       "product.id, " +                 //7
                       "product.name, " +               //8
                       "product.cultivation, " +        //9
                       "product.iluc, " +               //10
                       "product.processing, " +         //11
                       "product.packaging, " +          //12
                       "product.retail, " +             //13
                       "product.GHCultivated, " +       //14
                       "country.id, " +                 //15
                       "country.name, " +               //16
                       "country.transportEmission, " +  //17
                       "country.GHPenalty, " +          //18
                       "purchase.timestamp, " +         //19
                       "SUM(purchase.weight) " +        //20
                "FROM purchase " +
                "INNER JOIN storeItem ON purchase.storeItemID = storeItem.id " +
                "INNER JOIN product ON storeItem.productID = product.id " +
                "INNER JOIN country ON storeItem.countryID = country.id " +
                "WHERE strftime('%Y', purchase.timestamp) = '$year' AND strftime('%m', purchase.timestamp) = '${month.position}' " +
                "GROUP BY product.id, country.id, storeItem.organic, storeItem.packaged;"

        return dbManager.select(query){
            val purchases : MutableList<Purchase> = mutableListOf()

            if (it.moveToFirst()) {
                do {
                    val country = Country(it.getInt(15), it.getString(16), it.getDouble(17), it.getInt(18) != 0)
                    val product = Product(it.getInt(7), it.getString(8), it.getDouble(9), it.getDouble(10), it.getDouble(11), it.getDouble(12), it.getDouble(13), it.getInt(14) != 0)
                    val storeItem = StoreItem(it.getInt(1), product, country, it.getString(2), it.getInt(3) != 0, it.getInt(4) != 0, it.getDouble(5), it.getString(6))
                    purchases.add(Purchase(it.getInt(0), storeItem, it.getString(19), it.getDouble(20)))
                } while (it.moveToNext())
            }

            purchases
        }
    }

    fun getAlternatives(storeItem: StoreItem) {
        val query = "SELECT * FROM storeItem;"
    }
}