package com.example.androidapp.views.fragments.mainView

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_graph_page.*
import java.util.*


class GraphView : Fragment() {

    private val viewModel: EmissionViewModel by activityViewModels()
    private val weeklyRecommendedEmission = 1.5f*7
    private val maxDataPoints = 10
    private var pos = 0
    private val intervals = arrayListOf<String>("Ugentlig forbrug","Forbrug per uge")

    private lateinit var lineGraph : LineChart
    private lateinit var consumptionGraph: CombinedChart
    private lateinit var barGraph: CombinedChart
    private lateinit var dropDown : AutoCompleteTextView
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_graph_page, container, false)

        lineGraph = rootView.findViewById(R.id.top_graph)
        consumptionGraph = rootView.findViewById(R.id.bottom_graph)
        barGraph = rootView.findViewById(R.id.bar_graph)
        dropDown = rootView.findViewById(R.id.dropdown_AutoText)
        constraintLayout = rootView.findViewById(R.id.constraint_graph)

        setupGraphs()
        setupDropDown()


        return rootView
    }
    override fun onResume(){
        super.onResume()
        updateGraphs()

    }
    private fun updateGraphs() {
        updateLineGraph()
        updateBarGraph()
        updateConsumptionGraph()
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
    private fun updateConsumptionGraph(){
        val dataSet : BarDataSet
        if(pos ==0){
             dataSet = BarDataSet(getWeeklyConsumptionData(),"Dit ugelige Co2 forbrug")

        }else{
            dataSet = BarDataSet(getMonthlyConsumptionData(),"")
        }
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
    private fun setupConsumptionGraph(){
        updateConsumptionGraph()
        consumptionGraph.xAxis.valueFormatter = NumberToWeekFormatter()
        consumptionGraph.axisRight.isEnabled = false
        consumptionGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        consumptionGraph.description.isEnabled = false
        consumptionGraph.minimumHeight = 400
    }

    private fun setupGraphs() {
        setupLineGraph()
        setupBarGraph()
        setupConsumptionGraph()
    }
    private fun setupBarGraph(){
        //barGraph.setXAxisRenderer(BarXAxisRenderer(barGraph.viewPortHandler,barGraph.xAxis,barGraph.getTransformer(AxisDependency.LEFT)))
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
        for (i in 0..datapoints-1){
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

    private  fun getWeeklyConsumptionData(): MutableList<BarEntry>? {
        val purchases= viewModel.purchases.value

        val currentDay = Calendar.getInstance()

        return extractConsumptionDataWeekly(purchases!!.filter {
            it.calendar.get(Calendar.WEEK_OF_YEAR) == currentDay.get(
                Calendar.WEEK_OF_YEAR
            ) && it.calendar.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR)
        })
    }
    private  fun getMonthlyConsumptionData(): MutableList<BarEntry> {
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
    private fun extractConsumptionDataWeekly(purchaseList: List<Purchase>): MutableList<BarEntry> {
        val newList : MutableList<BarEntry> = mutableListOf()
        var day = Calendar.SUNDAY
        for (i in 0..6 ) {
            var sum = 0.0
            for (purchase in purchaseList) {


            }
            newList.add(BarEntry(i.toFloat(),sum.toFloat()))
        }
        return newList
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
    private fun setupDropDown(){
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this.requireContext(),
            R.layout.dropdown_menu_popup_item,
            intervals as List<Any?>
        )

        dropDown.setAdapter(adapter)
        dropDown.setText(adapter.getItem(0).toString(),false)


        dropDown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position) as String
                pos = intervals.indexOf(selected)
                if (pos == 0 ){
                    barGraph.visibility = BarChart.GONE
                    top_graph.visibility = LineChart.VISIBLE
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.clear(R.id.line2,ConstraintSet.TOP)
                    constraintSet.connect(R.id.line2,ConstraintSet.TOP,R.id.top_graph,ConstraintSet.BOTTOM)
                    constraintSet.applyTo(constraintLayout)
                }else{
                    top_graph.visibility = LineChart.GONE
                    barGraph.visibility = BarChart.VISIBLE
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.clear(R.id.line2,ConstraintSet.TOP)
                    constraintSet.connect(R.id.line2,ConstraintSet.TOP,R.id.bar_graph,ConstraintSet.BOTTOM)
                    constraintSet.applyTo(constraintLayout)
                }
                updateConsumptionGraph()
            }
        })

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
