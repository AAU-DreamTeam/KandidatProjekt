package com.example.androidapp.views

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.androidapp.R
import com.example.androidapp.models.tools.quiz.QuestionAnswer
import com.example.androidapp.models.tools.quiz.QuizMaster
import kotlinx.android.synthetic.main.activity_game_view.*

class GameView : AppCompatActivity() {
    private val viewModel = QuizMaster


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_view)

        QuizMaster.nextQuestion()

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

            answerQuizEmission.text = HtmlCompat.fromHtml(it.getAnswerLine(1), HtmlCompat.FROM_HTML_MODE_LEGACY)
            answerUserEmission.text = HtmlCompat.fromHtml(it.getAnswerLine(2), HtmlCompat.FROM_HTML_MODE_LEGACY)

            questionLL.visibility = VISIBLE
            answerLL.visibility = GONE

            circle.icon = ContextCompat.getDrawable(this, R.drawable.ic_question_mark_black_24dp)
            circle.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
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



            questionLL.visibility = GONE
            answerLL.visibility = VISIBLE
        }

        viewModel.remainingQuestions.observe(this) {
            if (it == 0) {
                btn_next.text = "Afslut"
                btn_next.setOnClickListener{
                    finish()
                }
            }
        }

        btn_above.setOnClickListener{
            QuizMaster.submitAnswer(QuestionAnswer.ABOVE)
        }

        btn_below.setOnClickListener{
            QuizMaster.submitAnswer(QuestionAnswer.BELLOW)
        }

        btn_next.setOnClickListener{
            if (!QuizMaster.nextQuestion()) {

            }
        }
    }
}