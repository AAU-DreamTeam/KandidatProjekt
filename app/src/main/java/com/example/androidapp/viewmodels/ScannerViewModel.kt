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
import com.example.androidapp.models.enums.COMPLETED
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

    private val _missingPurchases = MutableLiveData<MutableList<Purchase>>(mutableListOf())
    val missingPurchases: LiveData<MutableList<Purchase>> get() = _missingPurchases

    private val _completedPurchases = MutableLiveData<MutableList<Purchase>>(mutableListOf())
    val completedPurchases: LiveData<MutableList<Purchase>> get() = _completedPurchases

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
            _missingPurchases.value = it.filter { !it.completed }.toMutableList()
            _completedPurchases.value = it.filter { it.completed }.toMutableList()
        }
    }

    fun loadProducts() {
        _products.value = productRepository!!.loadProducts()
    }

    fun loadCountries(){
        _countries.value = countryRepository!!.loadCountries()
    }

    fun onDeletePurchase(index: Int, currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!!.removeAt(index)
            _completedPurchases.value = _completedPurchases.value!!

        }else{
            _missingPurchases.value!!.removeAt(index)
            _missingPurchases.value = _missingPurchases.value!!
        }
    }
    fun onCompletedChange(index: Int){
        _completedPurchases.value!!.add(_missingPurchases.value!![index])
        _missingPurchases.value!!.removeAt(index)
        _missingPurchases.value = _missingPurchases.value!!
        _completedPurchases.value =_completedPurchases.value!!
    }

    fun onOrganicChanged(index: Int, value: Boolean,currentList: COMPLETED) {
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.organic = value

        }else{
            _missingPurchases.value!![index].storeItem.organic = value
        }
    }

    fun onPackagedChanged(index: Int, value: Boolean, currentList: COMPLETED) {
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.packaged = value

        }else{
            _missingPurchases.value!![index].storeItem.packaged = value
        }
    }

    fun onReceiptTextChanged(index: Int, value: String,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.receiptText = value

        }else{
            _missingPurchases.value!![index].storeItem.receiptText = value
        }
    }

    fun onCountryChanged(index: Int, value: Country,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.country = value
            _completedPurchases.value!![index].storeItem.countryDefault = false

        }else{
            _missingPurchases.value!![index].storeItem.country = value
            _missingPurchases.value!![index].storeItem.countryDefault = false
        }
    }
    fun onCountryDefaultChanged(index: Int, value: Boolean,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.countryDefault = value

        }else{
            _missingPurchases.value!![index].storeItem.countryDefault = value
        }
    }

    fun onProductChanged(index: Int, value: Product,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.product = value

        }else{
            _missingPurchases.value!![index].storeItem.product = value
        }
    }
    fun getPurchase(index: Int,currentList: COMPLETED): Purchase {
        if (currentList == COMPLETED.COMPLETED){
           return _completedPurchases.value!![index]

        }else{
           return _missingPurchases.value!![index]
        }
    }

    fun onQuantityChanged(index: Int, value: Int,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].quantity = value

        }else{
            _missingPurchases.value!![index].quantity = value
        }
    }
    fun onQuantityDefaultChanged(index: Int,value: Boolean,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].quantityDefault = value

        }else{
            _missingPurchases.value!![index].quantityDefault = value
        }
    }

    fun onWeightChanged(index: Int, value: Double,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.weight = value

        }else{
            _missingPurchases.value!![index].storeItem.weight = value
        }
    }
    fun onWeightDefaultChanged(index: Int,value: Boolean,currentList: COMPLETED){
        if (currentList == COMPLETED.COMPLETED){
            _completedPurchases.value!![index].storeItem.weightDefault = value

        }else{
            _missingPurchases.value!![index].storeItem.weightDefault = value
        }
    }

    fun onSave(){
        _saved.value = purchaseRepository!!.savePurchases(combineLists())
    }
    fun combineLists(): List<Purchase> {
        return _completedPurchases.value!!.let { list1 -> _missingPurchases.value!!.let { list2 -> list1 + list2 } }

    }

    override fun onCleared() {
        super.onCleared()
        purchaseRepository?.close()
        productRepository?.close()
        countryRepository?.close()
    }
}