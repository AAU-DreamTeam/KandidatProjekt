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
import java.util.*

class ScannerViewModel: ViewModel() {
    var imagePath: String = ""

    private val _products = MutableLiveData<List<String>>(listOf())
    val products: LiveData<List<String>> get() = _products

    private val _countries = MutableLiveData<List<String>>(listOf())
    val countries: LiveData<List<String>> get() = _countries

    private val _purchases = MutableLiveData<MutableList<Purchase>>(mutableListOf())
    val purchases: LiveData<MutableList<Purchase>> get() = _purchases

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
                results.add(PurchaseRepository(context).generatePurchase(line.text.toLowerCase(Locale.getDefault())))
            }
        }

        _purchases.value = results
    }

    fun onDeletePurchase(index: Int){
        _purchases.value!!.removeAt(index)
    }

    fun onOrganicChanged(index: Int, value: Boolean) {
        _purchases.value!![index].storeItem.organic = value
    }

    fun onPackagedChanged(index: Int, value: Boolean) {
        _purchases.value!![index].storeItem.packaged = value
    }

    fun onTitleChanged(index: Int, value: String){
        _purchases.value!![index].storeItem.receiptText = value
    }

    fun onCountryChanged(index: Int, value: String){
        _purchases.value!![index].storeItem.country.name = value
    }

    fun onProductChanged(index: Int, value: String){
        _purchases.value!![index].storeItem.product.name = value
    }

    fun onQuantityChanged(index: Int, value: Int){
        _purchases.value!![index].quantity = value
    }

    fun onWeightChanged(index: Int, value: Double){
        _purchases.value!![index].storeItem.weight = value
    }
}