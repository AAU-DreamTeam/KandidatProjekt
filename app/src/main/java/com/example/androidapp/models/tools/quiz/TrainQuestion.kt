package com.example.androidapp.models.tools.quiz

import androidx.core.text.HtmlCompat
import kotlin.math.roundToInt
import kotlin.random.Random

class TrainQuestion(emission: Double): Question {
    private val emissionPerKM = 0.041
    override val actualValue = (emission/ emissionPerKM).roundToInt()
    override val quizValue = calcQuizValue()
    override var result: Boolean? = null

    override fun getType(): QuestionType {
        return QuestionType.TRAIN
    }

    private fun calcQuizValue() : Int {
        var value: Int
        val roundToNearest = 10

        do {
            value = Random.nextInt(0, 2 * actualValue)
        } while (value == actualValue)

        value = (value / roundToNearest) * roundToNearest

        if (value == 0) {
            value = if (value == roundToNearest) roundToNearest * 2 else roundToNearest
        }

        return value
    }

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Kører en togpassager"
            2 -> "$quizValue km"
            3 -> "for at matche din udledning?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        return when(line) {
            1 -> "En togpassager udleder ${"%.1f ".format(quizValue * emissionPerKM).replace('.', ',')} kg $CO2Str ved at køre $quizValue km"
            2 -> "Din udledning svarer til at køre $actualValue km"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
    }
}