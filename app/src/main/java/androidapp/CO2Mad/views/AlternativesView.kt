package androidapp.CO2Mad.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidapp.CO2Mad.R
import androidapp.CO2Mad.viewmodels.AlternativesViewModel
import androidapp.CO2Mad.views.adapters.AlternativesAdapter
import androidapp.CO2Mad.views.adapters.TripListAdapter
import kotlinx.android.synthetic.main.activity_alternatives_view.*
import kotlinx.android.synthetic.main.fragment_purchase_view.*
import kotlinx.android.synthetic.main.purchase_list_item.*

class AlternativesView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternatives_view)

        btnBack.setOnClickListener { finish() }
        headerTV.text = "${AlternativesViewModel.storeItem!!.product.name} alternativer"

        alternativeListRV.layoutManager = LinearLayoutManager(this)

        AlternativesViewModel.initiate(this)

        AlternativesViewModel.alternatives.observe(this) { list ->
            alternativeListRV.adapter = AlternativesAdapter(this, AlternativesViewModel.storeItem!!, list)
        }

        AlternativesViewModel.loadAlternatives(3)
    }
}