package androidapp.CO2Mad.views.fragments.mainView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidapp.CO2Mad.R
import androidapp.CO2Mad.viewmodels.EmissionViewModel
import androidapp.CO2Mad.views.adapters.TripListAdapter
import kotlinx.android.synthetic.main.fragment_purchase_view.*

class PurchaseView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripListRV.layoutManager = LinearLayoutManager(requireContext())

        viewModel.trips.observe(viewLifecycleOwner) { list ->
            tripListRV.adapter = TripListAdapter(requireContext() as AppCompatActivity, list)
        }
    }
}