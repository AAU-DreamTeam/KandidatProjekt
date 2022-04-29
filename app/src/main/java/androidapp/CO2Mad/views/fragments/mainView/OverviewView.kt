package androidapp.CO2Mad.views.fragments.mainView


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.*
import androidapp.CO2Mad.R
import androidapp.CO2Mad.models.tools.quiz.*
import androidapp.CO2Mad.viewmodels.EmissionViewModel
import androidapp.CO2Mad.views.GameView
import androidapp.CO2Mad.views.MainView
import androidapp.CO2Mad.views.ScannerView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_overview.*


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

        setupHighScore()
        setupEmission()
        setupPage()
        setupIcons(view, layoutInflater, view.parent as ViewGroup?)
    }

    override fun onResume() {
        super.onResume()
        observeIcons(QuizMaster.questions.value)
    }

    private fun setupHighScore(){
        QuizMaster.highScore.observe(viewLifecycleOwner) {
            frontPageHighScoreNum.text = it.toString()
        }
    }
    private fun setupEmission(){
        viewModel.emission.observe(viewLifecycleOwner) { emission ->
            val emissionString = HtmlCompat.fromHtml(
                "%.1f ".format(emission).replace(
                    '.',
                    ','
                ) + "kg CO<sub><small><small>2</small></small></sub>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            totalEmissionTV.text = emissionString
            QuizMaster.setEmission(emission ?: 0.0)

            hideOrShowGame(emission, QuizMaster.runtimeShowIcons)
        }
    }

    private fun setupPage() {
        QuizMaster.showIcons.observe(viewLifecycleOwner) { showIcons ->
            hideOrShowGame(viewModel.emission.value!!, showIcons)
        }
    }

    private fun hideOrShowGame(emission: Double, showIcons: Boolean) {
        if (emission == 0.0) {
            gameTV.text = resources.getString(R.string.game_no_products_text)
            gameContainer.visibility = View.GONE
        } else {
            gameTV.text = if (showIcons) {
                "Spil igen for at slå din high score!"
            } else {
                resources.getString(R.string.game_text)
            }
            gameContainer.visibility = View.VISIBLE
        }
    }

    private fun observeIcons(questions : MutableList<Question>?) {
        if (questions != null) {
            for ((i, q) in questions.withIndex()) {
                val icon = topView.findViewById<LinearLayout>(i)

                setupIconText(q, icon)
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
        var variant: QuestionVariant?

        if (icon.getType() == QuestionType.TREE) {
            variant = icon.getVariant(QuestionVariantType.ABSORPTION_DAYS)

            iconView.findViewById<TextView>(R.id.icon_textView).visibility = View.INVISIBLE
            iconView.findViewById<TextView>(R.id.icon_textView2).text =
                variant.actualValueStr
            setTextColor(iconView.findViewById(R.id.icon_textView2), variant)
        } else {
            variant = icon.getVariant(QuestionVariantType.EMISSION_KILOMETERS)
            iconView.findViewById<TextView>(R.id.icon_textView).text = variant.actualValueStr
            setTextColor(iconView.findViewById(R.id.icon_textView), variant)

            variant = icon.getVariant(QuestionVariantType.EMISSION_HOURS)
            iconView.findViewById<TextView>(R.id.icon_textView2).text = variant.actualValueStr
            setTextColor(iconView.findViewById<TextView>(R.id.icon_textView2), variant)
        }
    }

    private fun setTextColor(view: TextView, variant: QuestionVariant) {
        view.setTextColor(ContextCompat.getColor(requireContext(), getColor(variant.result)))
    }

    private fun getColor(result: Boolean?): Int {
        return when(result) {
            true -> R.color.green
            false -> R.color.red
            else -> R.color.black
        }
    }

    private fun setUpScanButton() {
        scanButton.setOnClickListener{
            if (!hasPermissions()) {
                ActivityCompat.requestPermissions(
                    parentFragment?.activity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            } else {
                MainView.resultLauncher!!.launch(Intent(activity, ScannerView::class.java))
            }
        }
    }

    private fun hasPermissions() : Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
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
            if (QuizMaster.currentQuestion.value != null) {
                QuizMaster.setEmission(viewModel.emission.value!!)
            }
            resultLauncher.launch(Intent(activity, GameView::class.java))
        }

        showButton.setOnClickListener {
            QuizMaster.showQuestions()
            QuizMaster.saveShowIcons(true, false)
            observeIcons(QuizMaster.questions.value)
        }
    }
}
