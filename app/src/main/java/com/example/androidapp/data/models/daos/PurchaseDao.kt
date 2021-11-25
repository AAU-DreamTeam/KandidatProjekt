package com.example.androidapp.data.models.daos

import android.content.Context
import android.database.Cursor
import com.example.androidapp.data.DBManager
import com.example.androidapp.data.models.Country
import com.example.androidapp.data.models.Product
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.data.models.StoreItem
import com.example.androidapp.viewmodels.MONTH

class PurchaseDao(context: Context) {
    private val dbManager: DBManager = DBManager(context)

    fun getAllFromYearAndMonth(year: String, month: MONTH): MutableList<Purchase> {
        val query =
                "SELECT purchase.id, " +                //0
                       "purchase.timestamp, " +         //1
                       "SUM(purchase.weight), " +       //2
                       "storeItem.id, " +               //3
                       "storeItem.receiptText, " +      //4
                       "storeItem.organic, " +          //5
                       "storeItem.packaged, " +         //6
                       "storeItem.weight, " +           //7
                       "storeItem.store, " +            //8
                       "product.id, " +                 //9
                       "product.name, " +               //10
                       "product.cultivation, " +        //11
                       "product.iluc, " +               //12
                       "product.processing, " +         //13
                       "product.packaging, " +          //14
                       "product.retail, " +             //15
                       "product.GHCultivated, " +       //16
                       "country.id, " +                 //17
                       "country.name, " +               //18
                       "country.transportEmission, " +  //19
                       "country.GHPenalty " +           //20
                "FROM purchase " +
                "INNER JOIN storeItem ON purchase.storeItemID = storeItem.id " +
                "INNER JOIN product ON storeItem.productID = product.id " +
                "INNER JOIN country ON storeItem.countryID = country.id " +
                "WHERE strftime('%Y', purchase.timestamp) = '$year' AND strftime('%m', purchase.timestamp) = '${month.position}' " +
                "GROUP BY product.id, country.id, storeItem.organic, storeItem.packaged;"

        return dbManager.selectMultiple(query){
            producePurchase(it)
        }
    }

    companion object {
        fun producePurchase(cursor: Cursor, startIndex: Int = 0): Purchase{
            val storeItem = StoreItemDao.produceStoreItem(cursor, startIndex + 3)

            return Purchase(cursor.getInt(startIndex), storeItem, cursor.getString(startIndex + 1), cursor.getDouble(startIndex + 2))
        }
    }
}