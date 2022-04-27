package androidapp.CO2Mad.models.tools.quiz

import android.content.Context
import androidapp.CO2Mad.repositories.PurchaseRepository
import androidapp.CO2Mad.repositories.VariablesRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception
import java.util.*

object QuizMaster : ViewModel() {
    private var variablesRepository: VariablesRepository? = null
    private var indices: MutableList<Int>? = null

    private val _emission = MutableLiveData<Double>()
    val emission: LiveData<Double> get() = _emission

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    private val _enableGame = MutableLiveData<Boolean>()
    val enableGame: LiveData<Boolean> get() = _enableGame

    private val _highScore = MutableLiveData<Int>()
    val highScore: LiveData<Int> get() = _highScore

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> get() = _currentQuestion

    private val _currentQuestionResult = MutableLiveData<Boolean>()
    val currentQuestionResult: LiveData<Boolean> get() = _currentQuestionResult

    private val _remainingQuestions = MutableLiveData<Int>()
    val remainingQuestions: LiveData<Int> get() = _remainingQuestions

    private val _questions = MutableLiveData<MutableList<Question>>()
    val questions: LiveData<MutableList<Question>> get() = _questions

    fun initiate(context: Context) {
        if (variablesRepository == null) {
            variablesRepository = VariablesRepository(context)
        }

        loadData()
    }

    private fun loadData(){
        _highScore.value = variablesRepository!!.loadHighScore()
        _enableGame.value = variablesRepository!!.loadEnableGame()
    }

    fun saveEnableGame(enableGame: Boolean){
        _enableGame.value = enableGame
        variablesRepository!!.saveEnableGame(enableGame)
    }

    private fun saveHighScore() {
        val score = _score.value!!

        if (score > _highScore.value!!) {
            variablesRepository!!.saveHighScore(score)
        }
    }

    fun firstQuestion() {
        if (currentQuestion.value == null) {
            nextQuestion()
        }
    }

    fun nextQuestion() : Boolean {
        if (emission.value != null) {
            drawQuestion()
            return true
        } else {
            throw Exception("Initiate emission before generating questions.")
        }
    }

    private fun generateQuestions() {
        val questionFactory = QuestionFactory()
        val questionsList = mutableListOf<Question>()

        indices = mutableListOf()
        var numberOfQuestions = 0

        for ((index, type) in QuestionType.values().withIndex()) {
            val question = questionFactory.getQuestion(type, emission.value!!)

            if (!enableGame.value!!) {
                question.showQuestion()
            }

            questionsList.add(index, question)

            for (i in 0 until question.numberOfVariants) {
                indices!!.add(index)
                numberOfQuestions++
            }
        }

        _remainingQuestions.value = numberOfQuestions
        indices!!.shuffle()

        _questions.value = questionsList
    }

    private fun drawQuestion() {
        val newQuestion = _questions.value!![indices!!.removeLast()]

        newQuestion.draw()

        _currentQuestion.value = newQuestion
        _remainingQuestions.value = _remainingQuestions.value?.minus(1)
    }

    fun setEmission(emission: Double) {
        _emission.value = emission
        _score.value = 0
        generateQuestions()
    }

    fun submitAnswer(answer: QuestionAnswer) {
        _currentQuestionResult.value = _currentQuestion.value?.submit(answer)

        if (_currentQuestionResult.value == true) {
            _score.value = _score.value?.plus(1)
        }
    }

    fun onQuizFinished(){
        saveHighScore()
        saveEnableGame(false)
    }

    fun showQuestions(){
        for(question in _questions.value!!){
            question.showQuestion()
        }
    }

    override fun onCleared() {
        super.onCleared()
        variablesRepository?.close()
    }
}
