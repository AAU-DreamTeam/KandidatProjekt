package androidapp.CO2Mad.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidapp.CO2Mad.R
import androidapp.CO2Mad.views.adapters.MainAdapter
import com.google.android.material.tabs.TabLayout


class MainView : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setUpTabs()
        setUpViewPager()

    }

    private fun setUpTabs(){
        with(tabLayout) {

            addTab(setupTab(R.drawable.ic_home))
            addTab(setupTab(R.drawable.ic_graph))
            addTab(setupTab(R.drawable.ic_basket))


            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPager.currentItem = tab!!.position
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Handle tab reselect
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Handle tab unselect
                }
            })
        }
    }
    private fun setupTab(drawable: Int): TabLayout.Tab {
        val newTab = tabLayout.newTab()
        val view = getLayoutInflater().inflate(R.layout.customtab,null);
        view.setBackgroundResource(drawable)
        newTab.customView = view
        return newTab
    }

    private fun setUpViewPager(){
        viewPagerAdapter = MainAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }
}
