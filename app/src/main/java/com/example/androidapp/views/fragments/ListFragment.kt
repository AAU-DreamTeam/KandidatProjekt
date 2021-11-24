package com.example.androidapp.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.adapters.EmissionListAdapter
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {
    private val viewModel: EmissionViewModel by viewModels({requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        purchaseList.layoutManager = LinearLayoutManager(requireContext())
        purchaseList.adapter = EmissionListAdapter(requireContext(), viewModel.purchaseList.value!!)

        viewModel.purchaseList.observe(viewLifecycleOwner, { list ->

        })
    }
}