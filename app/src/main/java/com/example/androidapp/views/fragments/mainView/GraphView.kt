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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*


class GraphView : Fragment() {

    private val viewModel: EmissionViewModel by activityViewModels()
    private val weeklyRecommendedEmission = 1.5f*7
    private val maxDataPoints = 10
    private val pos = 0

    private lateinit var lineGraph : LineChart
    private lateinit var bottomGraph: LineChart
    private lateinit var barGraph: CombinedChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_graph_page, container, false)

        lineGraph = rootView.findViewById(R.id.top_graph)
        bottomGraph = rootView.findViewById(R.id.bottom_graph)
        barGraph = rootView.findViewById(R.id.bar_graph)

        setupGraphs()


        return rootView
    }
    override fun onResume(){
        super.onResume()
        updateGraphs()

    }
    private fun updateGraphs() {
        updateBarGraph()
    }
    private fun updateLineGraph() {
        val dataSet = LineDataSet(getWeeklyData(),"Dit Co2 forbrug")
        val recomendedLine = LineDataSet(getWeeklyRecommendedData(7), "Anbefalet Co2 forbrug af Sundhedsstyrelsen")
        recomendedLine.color = Color.RED
        recomendedLine.setDrawValues(false)
        dataSet.setDrawValues(false)
        recomendedLine.setDrawCircles(false)
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 5f
        recomendedLine.lineWidth = 5f
        dataSet.color = Color.BLACK
        lineGraph.data = LineData(listOf(dataSet,recomendedLine))
    }
    private fun updateBarGraph(){
        val dataSet = BarDataSet(getBarData(),"Dit ugelige Co2 forbrug")
        val recomendedLine = LineDataSet(getWeeklyRecommendedData(maxDataPoints), "Anbefalet ugeligt Co2 forbrug af Sundhedsstyrelsen")
        recomendedLine.color = Color.RED
        recomendedLine.setDrawValues(false)
        dataSet.setDrawValues(false)
        recomendedLine.setDrawCircles(false)
        recomendedLine.lineWidth = 5f
        dataSet.color = Color.BLACK
        val data = CombinedData()
        data.setData(BarData(dataSet))
        data.setData(LineData(recomendedLine))
        barGraph.data = data

    }

    private fun setupGraphs() {
        setupLineGraph()
        setupBarGraph()
    }
    private fun setupBarGraph(){
        updateBarGraph()
        barGraph.xAxis.valueFormatter = NumberToWeekFormatter()
        barGraph.axisRight.isEnabled = false
        barGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barGraph.description.isEnabled = false
        barGraph.minimumHeight = 400
        barGraph.xAxis.setLabelCount(5,true)
        barGraph.xAxis.mAxisMaximum = 9f
    }
    private fun setupLineGraph(){
        updateLineGraph()
        lineGraph.xAxis.valueFormatter = NumberToDaysFormatter()
        lineGraph.axisRight.isEnabled = false
        lineGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineGraph.description.isEnabled = false
        lineGraph.minimumHeight = 500

    }
    private fun getWeeklyRecommendedData(datapoints : Int): MutableList<Entry> {
        var list = mutableListOf<Entry>()
        for (i in 0..6){
            list.add(Entry(i.toFloat(),weeklyRecommendedEmission))
        }
        return list
    }

    private  fun getWeeklyData(): MutableList<Entry> {
        val purchases= viewModel.purchases.value

        val currentDay = Calendar.getInstance()

        return aggeragateArray(extractDataWeekly(purchases!!.filter {
            it.calendar.get(Calendar.WEEK_OF_YEAR) == currentDay.get(
                Calendar.WEEK_OF_YEAR
            ) && it.calendar.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR)
        }))
    }
    private  fun getBarData(): MutableList<BarEntry> {
        val purchases= viewModel.purchases.value!!

        val currentDay = Calendar.getInstance()
        val newList : MutableList<Entry> = mutableListOf()
        var newYear =0
        for (i in 0..maxDataPoints-1){
            var weekOfYear = currentDay.get(Calendar.WEEK_OF_YEAR)-i
            if (weekOfYear < 1){
                weekOfYear = currentDay.get(Calendar.WEEK_OF_YEAR)-i+52
                newYear = 1
            }
            val purchaseList = purchases.filter {
                it.calendar.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR)-newYear &&
                        it.calendar.get(Calendar.WEEK_OF_YEAR) == weekOfYear
            }
            newList.add(Entry(i.toFloat(),summerizeWeek(purchaseList)))
        }
        return entryListToBarEntryList(newList)
    }
    private fun entryListToBarEntryList(list: MutableList<Entry>): MutableList<BarEntry> {
        val newList = mutableListOf<BarEntry>()
        for (entry in list){
            newList.add(BarEntry(list.size-entry.x-1,entry.y))
        }
        return newList
    }
    private fun summerizeWeek(purchaseList: List<Purchase>):Float{
        var sum = 0f
        for (purchase in purchaseList){
            sum += purchase.emission.toFloat()
        }
        return sum
    }
    private fun aggeragateArray(list: MutableList<Entry>): MutableList<Entry> {
        for (i in list.indices){
            if(i >1){
                list[i].y += list[i-1].y
            }
        }
        return list
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
    class NumberToWeekFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return "Uge ${value.toInt()+1}"
        }
    }
}
