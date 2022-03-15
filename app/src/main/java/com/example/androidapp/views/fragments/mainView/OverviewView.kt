package com.example.androidapp.views.fragments.mainView

import android.widget.*



import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.R
import com.example.androidapp.viewmodels.EmissionViewModel
import kotlinx.android.synthetic.main.activity_game_view.view.*
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.fragment_overview.*

class OverviewView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private val intervals = arrayListOf<String>("Ugentligt CO2 forbrug","Månedligt CO2 forbrug")
    private lateinit var totalEmissionTV: TextView
    private lateinit var gameTV: TextView
    private lateinit var playButton: Button
    private lateinit var showButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_overview, container, false)

        totalEmissionTV = rootView.findViewById(R.id.totalEmission)
        gameTV = rootView.findViewById(R.id.gameText)
        playButton= rootView.findViewById(R.id.button_play)
        showButton= rootView.findViewById(R.id.button_showNumbers)

        setupDropDown(rootView)
        setupPage()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalEmission.observe(viewLifecycleOwner, { emission ->
            val emissionString = HtmlCompat.fromHtml("%.3f ".format(emission).replace('.', ',') + "kg CO<sub><small><small>2</small></small></sub>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            totalEmissionTV.text = emissionString
        })


    }

    fun setupPage(){
        if(viewModel.purchases.value?.isNotEmpty() == false){
            gameTV.text= resources.getString(R.string.game_no_products_text)
            playButton.visibility= View.INVISIBLE
            showButton.visibility = View.INVISIBLE

        }else{
            gameTV.text= resources.getString(R.string.game_text)
            setupButtons()
        }
    }

    private fun setupDropDown(view: View){
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this.requireContext(),
            R.layout.dropdown_menu_popup_item,
            intervals as List<Any?>
        )
        val editDropDown = view.findViewById<AutoCompleteTextView>(R.id.co2Showcase)

        editDropDown.setAdapter(adapter)
        editDropDown.setText(adapter.getItem(0).toString(),false)
    }
    private fun setupButtons(){

    }

}