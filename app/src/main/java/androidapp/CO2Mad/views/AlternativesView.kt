package androidapp.CO2Mad.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidapp.CO2Mad.R
import androidapp.CO2Mad.models.StoreItem
import androidapp.CO2Mad.models.enums.PRODUCT_CATEGORY
import androidapp.CO2Mad.viewmodels.AlternativesViewModel
import androidapp.CO2Mad.views.adapters.AlternativesAdapter
import androidapp.CO2Mad.views.adapters.TripListAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_alternatives_view.*
import kotlinx.android.synthetic.main.fragment_purchase_view.*
import kotlinx.android.synthetic.main.purchase_list_item.*

class AlternativesView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternatives_view)

        btnBack.setOnClickListener { finish() }

        if(AlternativesViewModel.storeItem!!.product.productCategory == PRODUCT_CATEGORY.VEGETABLES){
            refresh_Btn.visibility = MaterialButton.GONE
        }

        refresh_Btn.setOnClickListener {
            AlternativesViewModel.newAlternatives()
            AlternativesViewModel.loadAlternatives()
        }

        headerTV.text = "${AlternativesViewModel.storeItem!!.product.name} alternativer"

        alternativeListRV.layoutManager = LinearLayoutManager(this)

        AlternativesViewModel.initiate(this)
        AlternativesViewModel.loadAlternatives()
        alternativeListRV.adapter = AlternativesAdapter(this, AlternativesViewModel.storeItem!!, AlternativesViewModel.alternatives.value!!.toMutableList())


        AlternativesViewModel.alternatives.observe(this) { list ->
            val adapter = alternativeListRV.adapter as AlternativesAdapter
            adapter.alternatives.clear()
            adapter.alternatives.addAll(list)
            adapter.notifyDataSetChanged()
        }


    }
}