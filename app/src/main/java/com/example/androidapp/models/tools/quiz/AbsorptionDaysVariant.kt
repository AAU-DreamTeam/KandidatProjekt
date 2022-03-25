package com.example.androidapp.models.tools.quiz

import kotlin.math.roundToInt

class AbsorptionDaysVariant(emission: Double, questionType: QuestionType): QuestionVariant {
    private val absorptionPerDay = getEffectPerUnit(questionType)
    override val roundToNearest = 5
    override val actualValue = (emission / absorptionPerDay).roundToInt()
    override val quizValue = calcQuizValue()
    override val actualValueStr = valueToString(actualValue)
    override val quizValueStr = valueToString(quizValue)
    override val quizEffect = quizValue * absorptionPerDay

    private fun valueToString(value: Int): String {
        val unitSingular = "dag"
        val unitPlural = "dage"

        return "$value ${if (value == 1) unitSingular else unitPlural}"
    }
}