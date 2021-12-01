package com.example.androidapp.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.ScannerActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class EmissionFragment : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private lateinit var toggleButton : MaterialButtonToggleGroup
    private lateinit var overviewFragment: OverviewFragment
    private lateinit var listFragment: ListFragment
    private lateinit var fragmentFL: FrameLayout
    private lateinit var scanButton: MaterialButton
    private lateinit var prevButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var monthTV: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_emission, container, false)

        overviewFragment = OverviewFragment()
        listFragment = ListFragment()
        toggleButton = rootView.findViewById(R.id.toggleButton)
        fragmentFL = rootView.findViewById(R.id.fragment_fl)
        scanButton = rootView.findViewById(R.id.btn_scan)
        prevButton = rootView.findViewById(R.id.btn_prev)
        nextButton = rootView.findViewById(R.id.btn_next)
        monthTV = rootView.findViewById(R.id.monthTV)

        setUpScanButton()
        setUpToggleButton()
        setUpMonthButtons()

        viewModel.loadData(requireContext())

        childFragmentManager.beginTransaction().replace(fragmentFL.id, overviewFragment).commit()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.month.observe(viewLifecycleOwner, { month ->
            monthTV.text = month
        })
    }

    private fun setUpToggleButton() {
        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId) {
                    R.id.btn_overview -> childFragmentManager.beginTransaction().replace(fragmentFL.id, overviewFragment).commit()
                    R.id.btn_list -> childFragmentManager.beginTransaction().replace(fragmentFL.id, listFragment).commit()
                }
            } else if (toggleButton.checkedButtonId == View.NO_ID) {
                toggleButton.check(checkedId)
            }
        }
    }

    private fun setUpScanButton() {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.loadData(requireContext())
        }

        scanButton.setOnClickListener{
            resultLauncher.launch(Intent(activity, ScannerActivity::class.java))
        }
    }

    private fun setUpMonthButtons(){
        prevButton.setOnClickListener {
            viewModel.loadPrev(requireContext())
        }

        nextButton.setOnClickListener {
            viewModel.loadNext(requireContext())
        }
    }
}