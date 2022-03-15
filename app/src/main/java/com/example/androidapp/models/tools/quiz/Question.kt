package com.example.androidapp.models.tools.quiz

interface Question {
    val quizValue: Int
    val actualValue: Int
    var result: Boolean?
    val CO2Str: String get() = "CO<sub><small><small>2</small></small></sub>"

    fun getType() : QuestionType

    fun submit(answer: QuestionAnswer): Boolean? {
        result = when(answer){
            QuestionAnswer.ABOVE -> quizValue < actualValue
            QuestionAnswer.BELLOW -> quizValue > actualValue
        }

        return result
    }

    fun getQuestionLine(line: Int): String

    fun getAnswerLine(line: Int): String
}