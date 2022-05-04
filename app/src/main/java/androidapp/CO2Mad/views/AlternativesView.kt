package androidapp.CO2Mad.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidapp.CO2Mad.R
import androidapp.CO2Mad.tools.enums.ProductCategory
import androidapp.CO2Mad.viewmodels.AlternativesViewModel
import androidapp.CO2Mad.views.adapters.AlternativesAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_alternatives_view.*

class AlternativesView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternatives_view)

        btnBack.setOnClickListener { finish() }

        if(AlternativesViewModel.storeItem!!.product.productCategory == ProductCategory.VEGETABLES){
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