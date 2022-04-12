package androidapp.CO2Mad.views.fragments.mainView

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidapp.CO2Mad.R
import androidapp.CO2Mad.viewmodels.EmissionViewModel
import androidapp.CO2Mad.views.ScannerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmissionView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private lateinit var overviewView: OverviewView
    private lateinit var fragmentFL: FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_emission, container, false)

        viewModel.initiate(requireContext())
        overviewView = OverviewView()
        fragmentFL = rootView.findViewById(R.id.fragment_fl)

        viewModel.loadData()

        childFragmentManager.beginTransaction().replace(fragmentFL.id, overviewView).commit()


        return rootView
    }

}
