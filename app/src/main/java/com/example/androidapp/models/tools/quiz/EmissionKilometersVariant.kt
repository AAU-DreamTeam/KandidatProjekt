package com.example.androidapp.models.tools.quiz

import kotlin.math.roundToInt

class EmissionKilometersVariant(emission: Double, questionType: QuestionType): QuestionVariant {
    private val emissionPerKM = getEffectPerUnit(questionType)

    override val actualValue = (emission/ emissionPerKM).roundToInt()
    override val roundToNearest = 10
    override val quizValue = calcQuizValue()
    override val quizEffect = quizValue * emissionPerKM
    override val actualValueStr = valueToString(actualValue)
    override val quizValueStr = valueToString(quizValue)

    private fun valueToString(value: Int): String {
        val unit = "km"

        return "$value $unit"
    }

}