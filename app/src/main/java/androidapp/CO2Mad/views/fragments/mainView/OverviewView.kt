package androidapp.CO2Mad.views.fragments.mainView


import android.content.Context
import android.content.Intent
import android.graphics.Point
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
import androidapp.CO2Mad.R
import androidapp.CO2Mad.models.tools.quiz.Question
import androidapp.CO2Mad.models.tools.quiz.QuestionType
import androidapp.CO2Mad.models.tools.quiz.QuestionVariantType
import androidapp.CO2Mad.models.tools.quiz.QuizMaster
import androidapp.CO2Mad.viewmodels.EmissionViewModel
import androidapp.CO2Mad.views.GameView
import androidapp.CO2Mad.views.ScannerView


class OverviewView : Fragment() {
    private val viewModel: EmissionViewModel by activityViewModels()
    private val intervals = arrayListOf("Ugentligt CO2 forbrug", "Månedligt CO2 forbrug")
    private lateinit var totalEmissionTV: TextView
    private lateinit var gameTV: TextView
    private lateinit var playButton: Button
    private lateinit var showButton: Button
    private lateinit var topView: ConstraintLayout
    private lateinit var constraintView: ConstraintLayout
    private lateinit var co2Showcase: AutoCompleteTextView
    private lateinit var scanButton : ImageButton
    private var pos = 0
    private val iconPerLine = 3
    private var iconSetupFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_overview, container, false)

        scanButton = rootView.findViewById(R.id.ScanButton)
        totalEmissionTV = rootView.findViewById(R.id.totalEmission)
        gameTV = rootView.findViewById(R.id.gameText)
        playButton = rootView.findViewById(R.id.button_play)
        showButton = rootView.findViewById(R.id.button_showNumbers)
        topView = rootView.findViewById(R.id.overviewView)
        constraintView = rootView.findViewById(R.id.overview_constraint)
        co2Showcase = rootView.findViewById(R.id.co2Showcase)



        setupButtons()
        setupDropDown()
        setUpScanButton()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEmission()
        setupPage()
        setupIcons(view, layoutInflater, view.parent as ViewGroup?)
    }

    override fun onResume() {
        super.onResume()
        observeIcons(QuizMaster.questions.value)
    }
    private fun setupEmission(){
        viewModel.emission.observe(viewLifecycleOwner) { emission ->
            val emissionString = HtmlCompat.fromHtml(
                "%.3f ".format(emission).replace(
                    '.',
                    ','
                ) + "kg CO<sub><small><small>2</small></small></sub>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            totalEmissionTV.text = emissionString
            QuizMaster.setEmission(emission ?: 0.0)
        }
    }

    private fun observeIcons(questions : MutableList<Question>?) {
        if (questions != null) {
            for ((i, q) in questions.withIndex()) {
                setupIconText(q, topView.findViewById(i))
            }
        }
    }

    private fun setupIcons(rootView: View, inflater: LayoutInflater, container: ViewGroup?) {
        QuizMaster.questions.observe(viewLifecycleOwner) {
            if (!iconSetupFinished) {
                for ((i, q) in it.withIndex()) {
                    setupIcon(rootView, q, i, inflater, container)
                }

                iconSetupFinished = true
            }
            observeIcons(it)
        }
    }

    private fun setupIcon(
        rootView: View,
        icon: Question,
        tag: Int,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        val view = inflater.inflate(R.layout.overview_icon, container, false);
        val imageView = view.findViewById<ImageView>(R.id.icon_imageView)
        imageView.setImageDrawable(rootView.resources.getDrawable(icon.iconId))
        view.id = tag
        topView.addView(view)
        val constraintSet = ConstraintSet()
        constraintSet.clone(topView)
        if (tag % iconPerLine != 0) {
            constraintSet.connect(tag, ConstraintSet.LEFT, tag - 1, ConstraintSet.RIGHT)
        }
        if (tag >= iconPerLine) {
            constraintSet.connect(
                tag,
                ConstraintSet.TOP,
                tag - iconPerLine * (tag / iconPerLine),
                ConstraintSet.BOTTOM
            )
        }
        constraintSet.applyTo(topView)
    }

    private fun setupIconText(icon: Question, iconView: View) {
        if (icon.getType() == QuestionType.TREE) {
            iconView.findViewById<TextView>(R.id.icon_textView).visibility = View.INVISIBLE
            iconView.findViewById<TextView>(R.id.icon_textView2).text =
                icon.getVariant(QuestionVariantType.ABSORPTION_DAYS).actualValueStr
        } else {
            iconView.findViewById<TextView>(R.id.icon_textView).text =
                icon.getVariant(QuestionVariantType.EMISSION_KILOMETERS).actualValueStr
            iconView.findViewById<TextView>(R.id.icon_textView2).text =
                icon.getVariant(QuestionVariantType.EMISSION_HOURS).actualValueStr
        }
    }

    private fun setupPage() {
        QuizMaster.enableGame.observe(viewLifecycleOwner) { enableGame ->
            if (enableGame && viewModel.emission.value != 0.0) {
                gameTV.text = resources.getString(R.string.game_text)
                playButton.visibility = View.VISIBLE
                showButton.visibility = View.VISIBLE
            } else {
                if (!enableGame) {
                    QuizMaster.showQuestions()
                    gameTV.text = resources.getString(R.string.game_disabled_text)
                } else {
                    gameTV.text = resources.getString(R.string.game_no_products_text)
                }

                playButton.visibility = View.INVISIBLE
                showButton.visibility = View.INVISIBLE
            }
        }
    }
    private fun setUpScanButton() {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.loadData()
        }

        scanButton.setOnClickListener{
            resultLauncher.launch(Intent(activity, ScannerView::class.java))
        }
    }

    private fun setupDropDown() {
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this.requireContext(),
            R.layout.dropdown_menu_popup_item,
            intervals as List<Any?>
        )

        co2Showcase.setAdapter(adapter)
        co2Showcase.setText(adapter.getItem(0).toString(), false)


        co2Showcase.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selected = parent.getItemAtPosition(position) as String
                viewModel.onEmissionTimeRangeChanged(intervals.indexOf(selected))
            }

    }

    private fun setupButtons() {
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        //setup listeners for scanning
        playButton.setOnClickListener {
            resultLauncher.launch(Intent(activity, GameView::class.java))
        }

        showButton.setOnClickListener {
            QuizMaster.showQuestions()
            observeIcons(QuizMaster.questions.value)
        }
    }

    private fun setHeight(view: View) {
        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            //view.resources.displayMetrics.heightPixels
            findScreenSize(view)
        } else {
            2000
        }

        topView.layoutParams.height = (height * 0.35).toInt()
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
