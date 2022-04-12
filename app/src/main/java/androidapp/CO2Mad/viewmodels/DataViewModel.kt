package androidapp.CO2Mad.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.repositories.StoreItemRepository

class DataViewModel: ViewModel() {
    private var storeItemRepository: StoreItemRepository? = null
    private val _storeItems = MutableLiveData<List<StoreItem>>(listOf())
    val storeItems: LiveData<List<StoreItem>> get() = _storeItems

    fun initiate(context: Context) {
        if (storeItemRepository == null) {
            storeItemRepository = StoreItemRepository(context)
        }
    }

    fun loadStoreItems(){
        _storeItems.value = storeItemRepository!!.loadStoreItems()
    }

    override fun onCleared() {
        super.onCleared()
        storeItemRepository?.close()
    }
}