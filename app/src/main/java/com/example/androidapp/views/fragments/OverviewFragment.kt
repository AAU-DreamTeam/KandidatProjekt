package com.example.androidapp.views.fragments

import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel

class OverviewFragment : Fragment() {
    private val viewModel: EmissionViewModel by viewModels({requireParentFragment()})
    private lateinit var totalEmissionTV: TextView
    private lateinit var totalEmissionAltTV: TextView
    private lateinit var emissionReductionTV: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_overview, container, false)

        totalEmissionTV = rootView.findViewById(R.id.totalEmission)
        totalEmissionAltTV = rootView.findViewById(R.id.totalEmissionAlt)
        emissionReductionTV = rootView.findViewById(R.id.percentage)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalEmission.observe(viewLifecycleOwner, { emission ->
            val emissionString = HtmlCompat.fromHtml("%.2f ".format(emission) + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            totalEmissionTV.text = emissionString
        })

        viewModel.totalEmissionAlt.observe(viewLifecycleOwner, { emission ->
            val emissionString = "$emission KG CO2"
            totalEmissionAltTV.text = emissionString
        })

        viewModel.emissionReduction.observe(viewLifecycleOwner, { emissionReduction ->
            val emissionReductionString = "$emissionReduction %"
            emissionReductionTV.text = emissionReductionString
        })
    }
}