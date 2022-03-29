package com.example.androidapp.views.fragments.mainView

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.ScannerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmissionView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private lateinit var toggleButton : MaterialButtonToggleGroup
    private lateinit var overviewView: OverviewView
    private lateinit var purchaseView: PurchaseView
    private lateinit var fragmentFL: FrameLayout
    private lateinit var scanButton: FloatingActionButton
    private lateinit var prevButton: MaterialButton
    private lateinit var nextButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_emission, container, false)
        viewModel.initiate(requireContext())
        scanButton=requireActivity().findViewById(R.id.floatingScan)
        overviewView = OverviewView()
        purchaseView = PurchaseView()
        toggleButton = rootView.findViewById(R.id.toggleButton)
        fragmentFL = rootView.findViewById(R.id.fragment_fl)

        viewModel.loadData()
        setUpScanButton()

        childFragmentManager.beginTransaction().replace(fragmentFL.id, overviewView).commit()


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setUpToggleButton() {
        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId) {
                    //R.id.btn_overview -> childFragmentManager.beginTransaction().replace(fragmentFL.id, overviewView).commit()
                    //R.id.btn_list -> childFragmentManager.beginTransaction().replace(fragmentFL.id, listView).commit()
                }
            } else if (toggleButton.checkedButtonId == View.NO_ID) {
                toggleButton.check(checkedId)
            }
        }
    }

    private fun setUpScanButton() {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.loadData()
            overviewView.setQuizMaster()
        }

        scanButton.setOnClickListener{
            resultLauncher.launch(Intent(activity, ScannerView::class.java))
        }
    }

    private fun setUpMonthButtons(){
        prevButton.setOnClickListener {
            viewModel.onViewPrev()
        }

        nextButton.setOnClickListener {
            viewModel.onViewNext()
        }
    }
}
