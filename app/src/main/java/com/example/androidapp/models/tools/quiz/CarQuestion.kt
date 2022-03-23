package com.example.androidapp.models.tools.quiz

import android.text.Html
import androidx.core.text.HtmlCompat
import com.example.androidapp.R
import kotlin.math.roundToInt
import kotlin.random.Random

class CarQuestion(emission: Double): Question {
    private val emissionPerKM = 0.171
    private val emissionPerHour = emissionPerKM * 100
    override val actualValue1 = (emission/ emissionPerKM).roundToInt()
    override val quizValue = calcQuizValue()
    override var result: Boolean?= null
    override val actualValue2 = (emission /emissionPerHour).roundToInt()
    override var didQuestion1 = false
    override var didQuestion2 = false
    override val iconStr1 = "$actualValue1 km"
    override val iconStr2 = "$actualValue2 hour(s)"
    override val iconId = R.drawable.ic_directions_car_black_24dp

    override fun getType() : QuestionType {
        return QuestionType.CAR
    }

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Kører en bil"
            2 -> "$quizValue km"
            3 -> "for at matche din udledning?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        return when(line) {
            1 -> "En bil udleder ${"%.1f ".format(quizValue * emissionPerKM).replace('.', ',')} kg $CO2Str ved at køre $quizValue km"
            2 -> "Din udledning svarer til at køre $actualValue1 km"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
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

}