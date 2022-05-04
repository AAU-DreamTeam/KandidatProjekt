package androidapp.CO2Mad.views

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidapp.CO2Mad.R
import androidapp.CO2Mad.viewmodels.QuizMaster
import androidapp.CO2Mad.viewmodels.EmissionViewModel
import androidapp.CO2Mad.views.adapters.MainAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayout


class MainView : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: MainAdapter
    private val viewModel: EmissionViewModel by viewModels()

    companion object {
        var resultLauncher: ActivityResultLauncher<Intent?>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data!!.extras!!.get("reloadData") as Boolean) {
                QuizMaster.saveShowIcons(false)
                viewModel.loadData()
            }
        }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setUpTabs()
        setUpViewPager()

    }

    private fun setUpTabs(){
        with(tabLayout) {
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

    private fun setUpViewPager(){
        viewPagerAdapter = MainAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            resultLauncher!!.launch(Intent(this, ScannerView::class.java))
        } else {
            Toast.makeText(applicationContext, "Kan ikke scanne uden tilladelse til at gemme billeder", Toast.LENGTH_SHORT).show()
        }
    }
}
