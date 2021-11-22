package com.example.androidapp.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.androidapp.R
import com.google.android.material.button.MaterialButtonToggleGroup

class EmissionFragment : Fragment() {
    private lateinit var toggleButton : MaterialButtonToggleGroup
    private lateinit var overviewFragment: OverviewFragment
    private lateinit var listFragment: ListFragment
    private lateinit var fragmentFL: FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_emission, container, false)
        // Inflate the layout for this fragment
        overviewFragment = OverviewFragment()
        listFragment = ListFragment()
        toggleButton = rootView.findViewById(R.id.toggleButton)
        fragmentFL = rootView.findViewById(R.id.fragment_fl)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        childFragmentManager.beginTransaction().replace(fragmentFL.id, overviewFragment).commit()

        super.onViewCreated(view, savedInstanceState)
    }
}