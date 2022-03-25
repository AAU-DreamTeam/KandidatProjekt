package com.example.androidapp.models.tools.quiz

import kotlin.math.roundToInt

class EmissionHoursVariant(emission: Double, questionType: QuestionType): QuestionVariant {
    private val emissionPerKM = getEffectPerUnit(questionType)
    private val emissionPerSecond = emissionPerKM * getVelocity(questionType)
    private val unitSingular: String
    private val unitPlural: String
    override val actualValue: Int
    override val roundToNearest: Int
    override var hasBeenAsked = false
    override val quizValue: Int
    override val quizEffect: Double
    override val actualValueStr: String
    override val quizValueStr: String

    init {
        val secondsPerMinute = 60
        val secondsPerHour = 60 * secondsPerMinute
        val actualValueSeconds = (emission /emissionPerSecond).roundToInt()
        val actualValueMinutes = actualValueSeconds / secondsPerMinute
        val actualValueHours = actualValueSeconds / secondsPerHour

        when {
            actualValueHours >= 1 -> {
                unitSingular = "time"
                unitPlural = "timer"
                roundToNearest = 2
                actualValue = actualValueHours
                quizValue = calcQuizValue()
                quizEffect = emissionPerSecond * secondsPerHour * quizValue
            }
            actualValueMinutes >= 1 -> {
                unitSingular = "minut"
                unitPlural = "minutter"
                roundToNearest = 15
                actualValue = actualValueMinutes
                quizValue = calcQuizValue()
                quizEffect = emissionPerSecond * secondsPerMinute * quizValue
            }
            else -> {
                unitSingular = "sekund"
                unitPlural = "sekunder"
                roundToNearest = 15
                actualValue = actualValueSeconds
                quizValue = calcQuizValue()
                quizEffect = emissionPerSecond * quizValue
            }
        }

        actualValueStr = valueToString(actualValue)
        quizValueStr = valueToString(quizValue)
    }

    private fun getVelocity(questionType: QuestionType): Double {
        return when(questionType) {
            QuestionType.CAR -> 0.0278
            QuestionType.TRAIN -> 0.0333
            QuestionType.PLANE -> 0.2166
            else -> 0.0
        }
    }

    private fun valueToString(value: Int): String {
        return if(value == -1) "? $unitPlural" else "$value ${if (value == 1) unitSingular else unitPlural}"
    }
}