package com.example.androidapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.R
import com.example.androidapp.viewmodels.AlternativesViewModel
import com.example.androidapp.views.adapters.AlternativesAdapter
import com.example.androidapp.views.adapters.TripListAdapter
import kotlinx.android.synthetic.main.activity_alternatives_view.*
import kotlinx.android.synthetic.main.fragment_purchase_view.*

class AlternativesView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternatives_view)

        alternativeListRV.layoutManager = LinearLayoutManager(this)

        AlternativesViewModel.alternatives.observe(this) { list ->
            alternativeListRV.adapter = AlternativesAdapter(this, AlternativesViewModel.storeItem!!, list)
        }

        AlternativesViewModel.loadAlternatives(3)
    }
}