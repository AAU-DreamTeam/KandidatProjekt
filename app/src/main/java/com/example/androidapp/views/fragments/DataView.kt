package com.example.androidapp.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.R
import com.example.androidapp.viewmodels.DataViewModel
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.adapters.DataAdapter
import kotlinx.android.synthetic.main.fragment_data.*

class DataView : Fragment() {
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initiate(requireContext())

        storeItemList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.storeItems.observe(viewLifecycleOwner, { list ->
            storeItemList.adapter = DataAdapter(requireContext(), list)
        })

        viewModel.loadStoreItems()
    }
}