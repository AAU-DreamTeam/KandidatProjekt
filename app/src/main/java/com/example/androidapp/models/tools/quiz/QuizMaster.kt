package com.example.androidapp.models.tools.quiz

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidapp.R
import java.lang.Exception
import java.util.*

object QuizMaster : ViewModel() {
    private val _emission = MutableLiveData<Double>(null)
    val emission: LiveData<Double>
        get() = _emission

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private val _highScore = MutableLiveData(0)
    val highScore: LiveData<Int> get() = _highScore

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> get() = _currentQuestion

    private val _currentQuestionResult = MutableLiveData<Boolean>()
    val currentQuestionResult: LiveData<Boolean> get() = _currentQuestionResult

    private val _remainingQuestions = MutableLiveData<Int>()
    val remainingQuestions: LiveData<Int> get() = _remainingQuestions

    private val questions = ArrayMap<QuestionType, Question>()
    private var indices: MutableList<Int>? = null

    fun nextQuestion() : Boolean {
        if (emission.value != null) {
            if (indices == null) {
                generateQuestions()
            } else if (indices!!.isEmpty()) {
                return false
            }
            drawQuestion()
            return true
        } else {
            throw Exception("Initiate emission before generating questions.")
        }
    }

    private fun generateQuestions() {
        val questionFactory = QuestionFactory()
        indices = mutableListOf()
        var numberOfQuestions = 0

        for ((index, type) in QuestionType.values().withIndex()) {
            questions[type] = questionFactory.getQuestion(type, emission.value!!)
            indices!!.add(index)
            numberOfQuestions++
        }

        _remainingQuestions.value = numberOfQuestions
        indices!!.shuffle()
    }

    private fun drawQuestion() {
        _currentQuestion.value = questions.valueAt(indices!!.removeLast())
        _remainingQuestions.value = _remainingQuestions.value?.minus(1)
    }

    fun setEmission(emission: Double) {
        _emission.value = emission
    }

    fun submitAnswer(answer: QuestionAnswer) {
        _currentQuestionResult.value = _currentQuestion.value?.submit(answer)

        if (_currentQuestionResult.value == true) {
            _score.value = _score.value?.plus(1)
        }
    }


    fun getQuestion(type: QuestionType) : Question {
        val question = questions[type]

        return question ?: throw IllegalArgumentException("Unable to find question of type ${type.name}.")
    }

    fun reset() {
        questions.clear()
        indices = null
    }
}