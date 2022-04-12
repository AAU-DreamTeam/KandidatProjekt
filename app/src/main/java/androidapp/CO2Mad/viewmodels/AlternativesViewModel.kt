package androidapp.CO2Mad.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.repositories.StoreItemRepository
import kotlin.random.Random

object AlternativesViewModel: ViewModel() {
    private var storeItemRepository: StoreItemRepository? = null
    var storeItem: StoreItem? = null


    private val _alternatives = MutableLiveData<List<StoreItem>>()
    val alternatives: LiveData<List<StoreItem>> get() = _alternatives

    fun initiate(context: Context){
        if (storeItemRepository == null) {
            storeItemRepository = StoreItemRepository(context)
        }
    }

    fun loadAlternatives(numberOfAlternatives: Int){
        _alternatives.value = storeItemRepository!!.loadAlternatives(storeItem!!, numberOfAlternatives)
        return
    }
}