package com.example.androidapp.views.fragments.mainView

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.icu.util.TimeUnit.values
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.ScannerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.chrono.JapaneseEra.values

class OverviewView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private lateinit var totalEmissionTV: TextView
    private lateinit var spinner: Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_overview, container, false)

        totalEmissionTV = rootView.findViewById(R.id.totalEmission)

        spinner= rootView.findViewById(R.id.spinner)

        createSpinner(rootView.context)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalEmission.observe(viewLifecycleOwner, { emission ->
            val emissionString = HtmlCompat.fromHtml("%.3f ".format(emission).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            totalEmissionTV.text = emissionString
        })


    }

    fun createSpinner(context: Context){
        val list = listOf<String>("Ugentlig forbrug","Måndeligt forbrug")
        spinner.adapter= ArrayAdapter<String>(context,R.layout.spinner_icon,list)


    }

}