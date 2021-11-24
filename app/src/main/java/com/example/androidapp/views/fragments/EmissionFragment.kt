package com.example.androidapp.views.fragments

import android.app.Activity
import android.content.Context
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
import androidx.fragment.app.viewModels
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.ScannerActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class EmissionFragment : Fragment() {
    private val viewModel: EmissionViewModel by viewModels()
    private lateinit var toggleButton : MaterialButtonToggleGroup
    private lateinit var overviewFragment: OverviewFragment
    private lateinit var listFragment: ListFragment
    private lateinit var fragmentFL: FrameLayout
    private lateinit var scanButton: MaterialButton
    private lateinit var monthTV: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_emission, container, false)

        overviewFragment = OverviewFragment()
        listFragment = ListFragment()
        toggleButton = rootView.findViewById(R.id.toggleButton)
        fragmentFL = rootView.findViewById(R.id.fragment_fl)
        scanButton = rootView.findViewById(R.id.btn_scan)
        monthTV = rootView.findViewById(R.id.monthTV)

        setUpScanButton()
        setUpToggleButton()
        viewModel.onStartUp(requireContext())

        childFragmentManager.beginTransaction().replace(fragmentFL.id, overviewFragment).commit()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.month.observe(viewLifecycleOwner, { month ->
            monthTV.text = month.name
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
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                //TODO: Call ViewModel
            }
        }
        scanButton.setOnClickListener{
            val intent = Intent(activity, ScannerActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}