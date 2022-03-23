package com.example.androidapp.models.tools.quiz

interface Question {
    val quizValue: Int
    val actualValue1: Int
    val actualValue2: Int
    var didQuestion1: Boolean
    var didQuestion2: Boolean
    var result: Boolean?
    val CO2Str: String get() = "CO<sub><small><small>2</small></small></sub>"
    val iconStr1: String
    val iconStr2: String
    val iconId: Int

    fun getType() : QuestionType

    fun submit(answer: QuestionAnswer): Boolean? {
        result = when(answer){
            QuestionAnswer.ABOVE -> quizValue < actualValue1
            QuestionAnswer.BELLOW -> quizValue > actualValue1
        }

        return result
    }

    fun getQuestionLine(line: Int): String

    fun getAnswerLine(line: Int): String
}