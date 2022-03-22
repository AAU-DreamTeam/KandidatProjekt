package com.example.androidapp.views.fragments.mainView


import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.GameView
import kotlinx.android.synthetic.main.activity_game_view.view.*
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.fragment_overview.*
import java.util.*


class OverviewView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private val intervals = arrayListOf<String>("Ugentligt CO2 forbrug","Månedligt CO2 forbrug")
    private lateinit var totalEmissionTV: TextView
    private lateinit var gameTV: TextView
    private lateinit var playButton: Button
    private lateinit var showButton: Button
    private lateinit var topView: View
    private lateinit var constraintView: ConstraintLayout
    private lateinit var co2Showcase: AutoCompleteTextView
    private var pos =0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_overview, container, false)

        totalEmissionTV = rootView.findViewById(R.id.totalEmission)
        gameTV = rootView.findViewById(R.id.gameText)
        playButton= rootView.findViewById(R.id.button_play)
        showButton= rootView.findViewById(R.id.button_showNumbers)
        topView = rootView.findViewById(R.id.overviewView)
        constraintView = rootView.findViewById(R.id.overview_constraint)
        co2Showcase = rootView.findViewById(R.id.co2Showcase)

        setupDropDown()
        setHeight(rootView )
        setupButtons()

        return rootView
    }
    override fun onResume(){
        super.onResume()
        setupPage()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.emissionList.observe(viewLifecycleOwner, { list ->
            val emissionString = HtmlCompat.fromHtml("%.3f ".format(list[pos]).replace('.',
                ',') + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            totalEmissionTV.text = emissionString})

    }
    private fun setupIcons(){

    }

    fun setupPage(){
        if(viewModel.purchases.value?.isNotEmpty() == false ){
            gameTV.text= resources.getString(R.string.game_no_products_text)
            playButton.visibility= View.INVISIBLE
            showButton.visibility = View.INVISIBLE

        }else{
            gameTV.text= resources.getString(R.string.game_text)
            playButton.visibility = View.VISIBLE
            showButton.visibility = View.VISIBLE
        }
    }

    private fun setupDropDown(){
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this.requireContext(),
            R.layout.dropdown_menu_popup_item,
            intervals as List<Any?>
        )

        co2Showcase.setAdapter(adapter)
        co2Showcase.setText(adapter.getItem(0).toString(),false)


        co2Showcase.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position) as String
                pos = intervals.indexOf(selected)
                viewModel.loadData()
            }
        })

    }

    private fun setupButtons(){
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.loadData()
        }
        //setup listeners for scanning
        playButton.setOnClickListener{
            resultLauncher.launch(Intent(activity, GameView::class.java))
        }
        showButton.setOnClickListener(View.OnClickListener() {

            @Override
            fun onClick(view: View){

            }
        })

    }
    private fun setHeight(view: View){

        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            //view.resources.displayMetrics.heightPixels
            findScreenSize(view)
        } else {
            2000
        }
        topView.layoutParams.height = (height*0.35).toInt()
        topView.requestLayout()
    }
    private fun findScreenSize(view: View): Int {
        val windowManager: WindowManager =
            view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val outPoint = Point()
        if (Build.VERSION.SDK_INT >= 19) {
            // include navigation bar
            display.getRealSize(outPoint)
        } else {
            // exclude navigation bar
            display.getSize(outPoint)
        }
        if (outPoint.y > outPoint.x) {
            return outPoint.y
        } else {
            return outPoint.x
        }
    }

}