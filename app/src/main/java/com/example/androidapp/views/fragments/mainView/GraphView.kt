package com.example.androidapp.views.fragments.mainView

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.androidapp.R
import com.example.androidapp.models.Purchase
import com.example.androidapp.viewmodels.EmissionViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_graph_page.*
import java.util.*


class GraphView : Fragment() {

    private val viewModel: EmissionViewModel by activityViewModels()
    private val weeklyRecommendedEmission = 1.5f*7

    private lateinit var topGraph : LineChart
    private lateinit var bottomGraph: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_graph_page, container, false)

        topGraph = rootView.findViewById(R.id.top_graph)
        bottomGraph = rootView.findViewById(R.id.bottom_graph)



        setupGraphs()





        return rootView
    }

    private fun setupGraphs() {
        setupTopGraph()

    }
    private fun setupTopGraph(){
        val dataSet = LineDataSet(getWeeklyData(),"Dit Co2 forbrug")
        val recomendedLine = LineDataSet(getWeeklyRecommendedData(), "Anbefalet Co2 forbrug af Sundhedsstyrelsen")

        recomendedLine.color = Color.RED
        recomendedLine.setDrawValues(false)
        dataSet.setDrawValues(false)
        recomendedLine.setDrawCircles(false)
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 5f
        recomendedLine.lineWidth = 5f
        dataSet.color = Color.BLACK
        val lineData = LineData(listOf(dataSet,recomendedLine))
        topGraph.xAxis.valueFormatter = NumberToDaysFormatter()
        topGraph.axisRight.isEnabled = false
        topGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        topGraph.description.isEnabled = false

        topGraph.minimumHeight = 500


        topGraph.data = lineData



    }
    private fun getWeeklyRecommendedData(): MutableList<Entry> {
        var list = mutableListOf<Entry>()
        for (i in 0..6){
            list.add(Entry(i.toFloat(),weeklyRecommendedEmission))
        }
        return list
    }

    private  fun getWeeklyData(): MutableList<Entry> {
        val purchases= viewModel.purchases.value

        val currentDay = Calendar.getInstance()

        return extractDataWeekly(purchases!!.filter {
            it.calendar.get(Calendar.WEEK_OF_YEAR) == currentDay.get(
                Calendar.WEEK_OF_YEAR
            ) && it.calendar.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR)
        })
    }
    private  fun getAllData(): List<Purchase> {
        val purchases= viewModel.purchases.value

        val currentDay = Calendar.getInstance()



        return purchases!!.filter { it.calendar.get(Calendar.MONTH) == currentDay.get(Calendar.MONTH) && it.calendar.get(
            Calendar.YEAR
        ) == currentDay.get(Calendar.YEAR)  }
    }
    private fun extractDataWeekly(purchaseList: List<Purchase>): MutableList<Entry> {
        val newList : MutableList<Entry> = mutableListOf()
        var day = Calendar.SUNDAY
        for (i in 0..6 ) {
            var sum = 0.0
            for (purchase in purchaseList) {
                if(purchase.calendar.get(Calendar.DAY_OF_WEEK) == day+i){
                    sum += purchase.emission
                }

            }
            newList.add(Entry(i.toFloat(),sum.toFloat()))
        }
        return newList
    }
    private fun getDayString(day : Int): String {
        when(day){
            Calendar.MONDAY -> return "Mandag"
            Calendar.TUESDAY -> return "Tirsdag"
            Calendar.WEDNESDAY -> return "Onsdag"
            Calendar.THURSDAY -> return "Torsdag"
            Calendar.FRIDAY -> return "Fredag"
            Calendar.SATURDAY -> return "Lørdag"
            else ->{
                return "Søndag"
            }
        }

    }
    class NumberToDaysFormatter : ValueFormatter() {
        private val days = arrayOf("Søndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }

}

