package androidapp.CO2Mad.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidapp.CO2Mad.views.fragments.mainView.GraphView
import androidapp.CO2Mad.views.fragments.mainView.OverviewView
import androidapp.CO2Mad.views.fragments.mainView.PurchaseView

class MainAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OverviewView()
            1 -> GraphView()
            else -> PurchaseView()
        }
    }

}