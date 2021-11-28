package com.example.androidapp.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidapp.data.models.Country
import com.example.androidapp.data.models.Purchase
import com.example.androidapp.repositories.CountryRepository
import com.example.androidapp.repositories.ProductRepository
import com.example.androidapp.repositories.PurchaseRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ScannerViewModel: ViewModel() {
    var imagePath: String = ""

    private val _products = MutableLiveData<List<String>>(listOf())
    val products: LiveData<List<String>> get() = _products

    private val _countries = MutableLiveData<List<String>>(listOf())
    val countries: LiveData<List<String>> get() = _countries

    private val _purchases = MutableLiveData<List<Purchase>>(listOf())
    val purchases: LiveData<List<Purchase>> get() = _purchases

    fun loadProducts(context: Context) {
        _products.value = ProductRepository(context).loadProducts()
    }

    fun loadCountries(context: Context){
        _countries.value = CountryRepository(context).loadCountries()
    }

    fun runTextRecognition(context: Context){
        val image = InputImage.fromBitmap(BitmapFactory.decodeFile(imagePath), 0)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
                .addOnSuccessListener { text ->
                    textToPurchases(context, text)
                }
                .addOnFailureListener { e -> // Task failed with an exception
                    e.printStackTrace()
                }
    }

    private fun textToPurchases(context: Context, text: Text) {
        val results = mutableListOf<Purchase>()

        for (block in text.textBlocks) {
            for (line in block.lines) {
                results.add(PurchaseRepository(context).generatePurchase(line.text))
            }
        }

        _purchases.value = results
    }
}