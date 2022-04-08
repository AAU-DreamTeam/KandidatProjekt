package com.example.androidapp.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidapp.views.fragments.mainView.EmissionView
import com.example.androidapp.views.fragments.mainView.GraphView
import com.example.androidapp.views.fragments.mainView.PurchaseView

class MainAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EmissionView()
            1 -> GraphView()
            else -> PurchaseView()
        }
    }

}