package com.example.androidapp.views.fragments.mainView


import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.androidapp.R
import com.example.androidapp.models.tools.quiz.Question
import com.example.androidapp.models.tools.quiz.QuestionType
import com.example.androidapp.models.tools.quiz.QuestionVariantType
import com.example.androidapp.models.tools.quiz.QuizMaster
import com.example.androidapp.viewmodels.EmissionViewModel
import com.example.androidapp.views.GameView


class OverviewView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private val intervals = arrayListOf<String>("Ugentligt CO2 forbrug","Månedligt CO2 forbrug")
    private lateinit var totalEmissionTV: TextView
    private lateinit var gameTV: TextView
    private lateinit var playButton: Button
    private lateinit var showButton: Button
    private lateinit var topView: ConstraintLayout
    private lateinit var constraintView: ConstraintLayout
    private lateinit var co2Showcase: AutoCompleteTextView
    private var pos =0
    private val iconPerLine = 3

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


        setHeight(rootView)
        setupButtons()
        setQuizMaster()
        setupIcons(rootView,inflater,container)
        setupDropDown()

        return rootView
    }
    override fun onResume(){
        super.onResume()
        setupPage()
        observeIcons()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.emissionList.observe(viewLifecycleOwner) { list ->
            val emissionString = HtmlCompat.fromHtml(
                "%.3f ".format(list[pos]).replace(
                    '.',
                    ','
                ) + "kg CO<sub><small><small>2</small></small></sub>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            totalEmissionTV.text = emissionString
        }
    }
    private fun observeIcons(){
        val questions = QuizMaster.questions.value
        if (questions != null) {
            for (i in questions.indices) {
                setupIconText(questions[i], topView.findViewById(i))
            }
        }

    }
    private fun setupIcons(rootView: View,inflater: LayoutInflater,container: ViewGroup?) {

        val questions = QuizMaster.questions.value
        if (questions != null) {
            for (i in questions.indices) {
                setupIcon(rootView, questions[i], i, inflater, container)
            }

            for (i in questions.indices) {
                setupIconText(questions[i], topView.findViewById(i))

            }
        }
    }
    private fun setupIcon(rootView: View,icon:Question,tag:Int,inflater: LayoutInflater,container: ViewGroup?){
        val view = inflater.inflate(R.layout.overview_icon,container, false);
        val imageView = view.findViewById<ImageView>(R.id.icon_imageView)
        imageView.setImageDrawable(rootView.resources.getDrawable(icon.iconId))
        view.id =tag
        topView.addView(view)
        val constraintSet = ConstraintSet()
        constraintSet.clone(topView)
        if(tag%iconPerLine!=0){
            constraintSet.connect(tag,ConstraintSet.LEFT,tag-1,ConstraintSet.RIGHT)
        }
        if (tag>=iconPerLine){
            constraintSet.connect(tag,ConstraintSet.TOP,tag-iconPerLine*(tag/iconPerLine),ConstraintSet.BOTTOM)
        }
        constraintSet.applyTo(topView)

    }
    private fun setupIconText(icon:Question,iconView:View){
        if (icon.getType() == QuestionType.TREE) {
            iconView.findViewById<TextView>(R.id.icon_textView).visibility=View.INVISIBLE
            iconView.findViewById<TextView>(R.id.icon_textView2).text =
                icon.getVariant(QuestionVariantType.ABSORPTION_DAYS).iconStr()
        } else {
            iconView.findViewById<TextView>(R.id.icon_textView).text =
                icon.getVariant(QuestionVariantType.EMISSION_KILOMETERS).iconStr()
            iconView.findViewById<TextView>(R.id.icon_textView2).text =
                icon.getVariant(QuestionVariantType.EMISSION_HOURS).iconStr()
        }
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
                setQuizMaster()
                observeIcons()
            }
        })

    }
    fun setQuizMaster(){
        if(pos == 0){
            val value = viewModel.weeklyEmission.value
            if(value != null){
                QuizMaster.setEmission(value)
            }else{
                QuizMaster.setEmission(0.0)
            }
        }else{
            val value = viewModel.monthlyEmission.value
            if(value != null){
                QuizMaster.setEmission(value)
            }else{
                QuizMaster.setEmission(0.0)
            }
        }
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
                setQuizMaster()
                observeIcons()
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


