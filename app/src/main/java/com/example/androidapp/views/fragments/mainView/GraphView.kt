package com.example.androidapp.views.fragments.mainView

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.androidapp.R
import com.example.androidapp.models.Purchase
import com.example.androidapp.models.enums.PRODUCT_CATEGORY
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.AxisRenderers.ImageXAxisRenderer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.card_layout.view.*
import kotlinx.android.synthetic.main.fragment_graph_page.*
import java.util.*


class GraphView : Fragment() {

    private val viewModel: EmissionViewModel by activityViewModels()
    private val weeklyRecommendedEmission = 1.5f*7
    private val maxDataPoints = 4
    private val minimumHeight = 600
    private var pos = 0
    private var persons = 1
    private val intervals = arrayListOf("Ugentlig forbrug","Forbrug per uge")
    private val recommendedWeeklyConsumption = listOf(0.3165*7,0.43066*7,0.11081*7,0.113785*7,0.0378*7,0.092005*7)

    private lateinit var lineGraph : LineChart
    private lateinit var consumptionGraph: CombinedChart
    private lateinit var barGraph: CombinedChart
    private lateinit var dropDown : AutoCompleteTextView
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var imageList : MutableList<Drawable>
    private lateinit var personsBox : TextInputEditText

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
        personsBox = rootView.findViewById(R.id.number_of_people_input)
        imageList = getImages()

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
        System.out.println("Here:" + getWeeklyData().toString())
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
        val dataSet = BarDataSet(getBarData(),"Dit ugentlige Co2 forbrug")
        val recomendedLine = LineDataSet(getWeeklyRecommendedData(maxDataPoints), "Anbefalet ugentlige Co2 forbrug af Sundhedsstyrelsen")
        recomendedLine.color = Color.RED
        recomendedLine.setDrawValues(false)
        dataSet.setDrawValues(false)
        recomendedLine.setDrawCircles(false)
        recomendedLine.lineWidth = 5f
        dataSet.color = Color.BLACK
        val data = CombinedData()
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        data.setData(BarData(dataSet))
        data.setData(LineData(recomendedLine))
        barGraph.data = data

    }
    private fun updateConsumptionGraph(){
        val dataSet : BarDataSet
        val recomendedLine : LineDataSet
        if(pos ==0){
             dataSet = BarDataSet(getWeeklyConsumptionData(),"Dit ugentlige CO2 forbrug fordelt på madvarer")
             recomendedLine = LineDataSet(getRecommendedConsumptionData(1), "Anbefalet ugentlige bæredygtigt CO2 forbrug fordelt på madvarer")
        }else{
            dataSet = BarDataSet(getMonthlyConsumptionData(),"Dit ugentlige CO2 forbrug fordelt på madvarer")
            recomendedLine = LineDataSet(getRecommendedConsumptionData(maxDataPoints), "Anbefalet ugentlige bæredygtigt CO2 forbrug fordelt på madvarer")
        }
        recomendedLine.color = Color.RED
        recomendedLine.setDrawValues(false)
        dataSet.setDrawValues(false)
        recomendedLine.setDrawCircles(false)
        recomendedLine.lineWidth = 5f
        dataSet.color = Color.BLACK
        val data = CombinedData()
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        data.setData(barData)
        data.setData(LineData(recomendedLine))
        consumptionGraph.data = data

    }

    private fun setupGraphs() {
        setupLineGraph()
        setupBarGraph()
        setupConsumptionGraph()
    }

    private fun setupConsumptionGraph(){
        updateConsumptionGraph()
        consumptionGraph.setXAxisRenderer(ImageXAxisRenderer(consumptionGraph.viewPortHandler,consumptionGraph.xAxis,
            consumptionGraph.getTransformer(YAxis.AxisDependency.LEFT),imageList,100,100))
        consumptionGraph.axisRight.isEnabled = false
        consumptionGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        consumptionGraph.description.isEnabled = false
        consumptionGraph.minimumHeight = minimumHeight
        consumptionGraph.setExtraOffsets(0f,0f,0f,20f)
        consumptionGraph.legend.isWordWrapEnabled = true
        consumptionGraph.xAxis.setLabelCount(6,true)

    }
    private fun setupBarGraph(){
        //barGraph.setXAxisRenderer(BarXAxisRenderer(barGraph.viewPortHandler,barGraph.xAxis,barGraph.getTransformer(AxisDependency.LEFT)))
        updateBarGraph()
        barGraph.xAxis.valueFormatter = NumberToWeekFormatter()
        barGraph.axisRight.isEnabled = false
        barGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barGraph.description.isEnabled = false
        barGraph.minimumHeight = minimumHeight
        barGraph.legend.isWordWrapEnabled = true
        barGraph.xAxis.setLabelCount(4,true)
        barGraph.xAxis.mAxisMaximum = 9f
    }
    private fun setupLineGraph(){
        updateLineGraph()
        lineGraph.xAxis.valueFormatter = NumberToDaysFormatter()
        lineGraph.axisRight.isEnabled = false
        lineGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineGraph.description.isEnabled = false
        lineGraph.legend.isWordWrapEnabled = true
        lineGraph.minimumHeight = minimumHeight

    }
    private fun getWeeklyRecommendedData(datapoints : Int): MutableList<Entry> {
        val list = mutableListOf<Entry>()
        for (i in 0..datapoints-1){
            list.add(Entry(i.toFloat(),weeklyRecommendedEmission*persons))
        }
        return list
    }
    private fun getRecommendedConsumptionData(weeks: Int): MutableList<Entry> {
        val list = mutableListOf<Entry>()
        if(weeks <1){
            throw Exception("Weeks are not allowed to be below 1")
        }
        for(i in recommendedWeeklyConsumption.indices){
            list.add(Entry(i.toFloat(),recommendedWeeklyConsumption[i].toFloat()*weeks*persons))

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
        val newList : MutableList<BarEntry> = mutableListOf()
        var newYear =0
        for(k in PRODUCT_CATEGORY.values().indices){
            var purchaseList = mutableListOf<Purchase>()
            for (i in 0..maxDataPoints-1){
                var weekOfYear = currentDay.get(Calendar.WEEK_OF_YEAR)-i
                if (weekOfYear < 1){
                    weekOfYear = currentDay.get(Calendar.WEEK_OF_YEAR)-i+52
                    newYear = 1
                }
                purchaseList.addAll(purchases.filter {
                    it.calendar.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR)-newYear &&
                            it.calendar.get(Calendar.WEEK_OF_YEAR) == weekOfYear &&
                            it.storeItem.product.productCategory == PRODUCT_CATEGORY.values()[k]
                })
            }
            newList.add(BarEntry(k.toFloat(),summerizePurchaesList(purchaseList)))
        }
        return newList
    }
    private fun extractConsumptionDataWeekly(purchaseList: List<Purchase>): MutableList<BarEntry> {
        val newList : MutableList<BarEntry> = mutableListOf()
        for (i in PRODUCT_CATEGORY.values().indices) {
            var sum = 0.0
            for (purchase in purchaseList.filter { it.storeItem.product.productCategory == PRODUCT_CATEGORY.values()[i] }) {
                sum += purchase.emission
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
            newList.add(Entry(i.toFloat(),summerizePurchaesList(purchaseList)))
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
    private fun summerizePurchaesList(purchaseList: List<Purchase>):Float{
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
            if (i == 0){
                newList.add(Entry(6f,sum.toFloat()))

            }else{
                newList.add(Entry(i.toFloat()-1,sum.toFloat()))
            }
        }
        newList.add(newList.removeAt(0))
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
        personsBox.doAfterTextChanged {
            if(!it.toString().isEmpty()){
                personsBox.error = null
                persons = it.toString().toInt()
                updateGraphs()
            }else{
                personsBox.error = "Indtast antal personer"
            }
        }

    }
    class NumberToDaysFormatter : ValueFormatter() {
        private val days = arrayOf( "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag", "Søndag")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }
    }
    class NumberToWeekFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return "Uge ${value.toInt()+1}"
        }
    }
    fun getImages(): MutableList<Drawable> {
        return mutableListOf(this.resources.getDrawable(R.drawable.ic_vegetables),
            this.resources.getDrawable(R.drawable.ic_grainlegume),
            this.resources.getDrawable(R.drawable.ic_chicken),
            this.resources.getDrawable(R.drawable.ic_cow),
            this.resources.getDrawable(R.drawable.ic_pig),
            this.resources.getDrawable(R.drawable.ic_lamb),)


    }
}
