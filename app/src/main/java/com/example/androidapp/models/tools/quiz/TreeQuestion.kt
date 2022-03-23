package com.example.androidapp.models.tools.quiz

import com.example.androidapp.R
import kotlin.math.roundToInt
import kotlin.random.Random

class TreeQuestion(emission: Double): Question {
    private val absorptionPerDay = 0.060
    override val actualValue1 = (emission / absorptionPerDay).roundToInt()
    override val quizValue = calcQuizValue()
    override var result: Boolean? = null
    override val actualValue2 = 0
    override var didQuestion1 = false
    override var didQuestion2 = false
    override val iconStr1 = "$actualValue1 km"
    override val iconStr2 = "null"
    override val iconId = R.drawable.ic_park_black_24dp

    override fun getType(): QuestionType {
        return QuestionType.TREE
    }

    private fun calcQuizValue() : Int {
        var value: Int
        val roundToNearest = 10

        do {
            value = Random.nextInt(0, 2 * actualValue1)
        } while (value == actualValue1)

        value = (value / roundToNearest) * roundToNearest

        if (value == 0) {
            value = if (value == roundToNearest) roundToNearest * 2 else roundToNearest
        }

        return value
    }

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Bruger et træ"
            2 -> "$quizValue dag${if (quizValue != 1) "e" else ""}"
            3 -> "for at optage din udledning?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        val pluralStr = if (quizValue != 1) "e" else ""

        return when(line) {
            1 -> "Et træ optager ${"%.1f ".format(quizValue * absorptionPerDay).replace('.', ',')} kg $CO2Str på $quizValue dag$pluralStr"
            2 -> "Din udledning svarer til $actualValue1 dag${pluralStr}s $CO2Str optag"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
    }
}