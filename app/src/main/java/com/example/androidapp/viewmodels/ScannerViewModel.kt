package com.example.androidapp.viewmodels

import android.content.Context
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidapp.models.Country
import com.example.androidapp.models.Product
import com.example.androidapp.models.Purchase
import com.example.androidapp.repositories.CountryRepository
import com.example.androidapp.repositories.ProductRepository
import com.example.androidapp.repositories.PurchaseRepository
import com.example.androidapp.repositories.StoreItemRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

class ScannerViewModel: ViewModel() {
    private var purchaseRepository: PurchaseRepository? = null
    private var countryRepository: CountryRepository? = null
    private var productRepository: ProductRepository? = null

    private val _saved = MutableLiveData<Boolean>()
    val saved: LiveData<Boolean> get() = _saved

    private val _products = MutableLiveData<List<Product>>(listOf())
    val products: LiveData<List<Product>> get() = _products

    private val _countries = MutableLiveData<List<Country>>(listOf())
    val countries: LiveData<List<Country>> get() = _countries

    private val _purchases = MutableLiveData<MutableList<Purchase>>(mutableListOf())
    val purchases: LiveData<MutableList<Purchase>> get() = _purchases

    fun initiate(context: Context) {
        if (purchaseRepository == null) {
            purchaseRepository = PurchaseRepository(context)
        }

        if (productRepository == null) {
            productRepository = ProductRepository(context)
        }

        if (countryRepository == null) {
            countryRepository = CountryRepository(context)
        }
    }

    fun onPhotoTaken(imagePath: String){
        purchaseRepository!!.extractPurchases(imagePath) {
            _purchases.value = it
        }
    }

    fun loadProducts() {
        _products.value = productRepository!!.loadProducts()
    }

    fun loadCountries(){
        _countries.value = countryRepository!!.loadCountries()
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

    fun onReceiptTextChanged(index: Int, value: String){
        _purchases.value!![index].storeItem.receiptText = value
    }

    fun onCountryChanged(index: Int, value: Country){
        _purchases.value!![index].storeItem.country = value
    }

    fun onProductChanged(index: Int, value: Product){
        _purchases.value!![index].storeItem.product = value
    }
    fun getPurchase(index: Int): Purchase {
        return _purchases.value!![index]
    }

    fun onQuantityChanged(index: Int, value: Int){
        _purchases.value!![index].quantity = value
    }

    fun onWeightChanged(index: Int, value: Double){
        _purchases.value!![index].storeItem.weight = value
    }

    fun onSave(){
        _saved.value = purchaseRepository!!.savePurchases(_purchases.value!!)
    }

    override fun onCleared() {
        super.onCleared()
        purchaseRepository?.close()
        productRepository?.close()
        countryRepository?.close()
    }
}