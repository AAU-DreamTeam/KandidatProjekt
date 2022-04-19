package androidapp.CO2Mad.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidapp.CO2Mad.R
import androidapp.CO2Mad.models.tools.quiz.QuestionAnswer
import androidapp.CO2Mad.models.tools.quiz.QuizMaster
import kotlinx.android.synthetic.main.activity_game_view.*

class GameView : AppCompatActivity() {
    private val viewModel = QuizMaster


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_view)

        viewModel.emission.observe(this) {
            carbonFootprint.text = "%.1f ".format(it).replace('.', ',') + " kg"
        }

        viewModel.score.observe(this) {
            score.text = it.toString()
        }

        viewModel.highScore.observe(this) {
            highScore.text = it.toString()
        }

        viewModel.currentQuestion.observe(this) {
            questionStart.text = HtmlCompat.fromHtml(it.getQuestionLine(1), HtmlCompat.FROM_HTML_MODE_LEGACY)
            questionMiddle.text = HtmlCompat.fromHtml(it.getQuestionLine(2), HtmlCompat.FROM_HTML_MODE_LEGACY)
            questionEnd.text = HtmlCompat.fromHtml(it.getQuestionLine(3), HtmlCompat.FROM_HTML_MODE_LEGACY)

            questionLL.visibility = VISIBLE
            answerLL.visibility = GONE

            circle.icon = ContextCompat.getDrawable(this, R.drawable.ic_question_mark_black_24dp)
            circle.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))

            backgroundImage.setImageResource(it.bgImageId)
        }

        viewModel.currentQuestionResult.observe(this) {
            if (it == true) {
                answerHeader.text = "Korrekt!"
                circle.icon = ContextCompat.getDrawable(this, R.drawable.ic_done_black_24dp)
                circle.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            } else {
                answerHeader.text = "Forkert!"
                circle.icon = ContextCompat.getDrawable(this, R.drawable.ic_close_black_24dp)
                circle.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            }

            answerQuizEmission.text = HtmlCompat.fromHtml(viewModel.currentQuestion.value!!.getAnswerLine(1), HtmlCompat.FROM_HTML_MODE_LEGACY)
            answerUserEmission.text = HtmlCompat.fromHtml(viewModel.currentQuestion.value!!.getAnswerLine(2), HtmlCompat.FROM_HTML_MODE_LEGACY)

            questionLL.visibility = GONE
            answerLL.visibility = VISIBLE
        }

        viewModel.remainingQuestions.observe(this) {
            if (it == 0) {
                btnNext.text = "Afslut"
                btnNext.setOnClickListener{
                    viewModel.onQuizFinished()
                    finish()
                }
            }
        }

        btnAbove.setOnClickListener{
            QuizMaster.submitAnswer(QuestionAnswer.ABOVE)
        }

        btnBelow.setOnClickListener{
            QuizMaster.submitAnswer(QuestionAnswer.BELLOW)
        }

        btnNext.setOnClickListener{
            QuizMaster.nextQuestion()
        }

        game_back_button.setOnClickListener{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        QuizMaster.nextQuestion()
    }
}