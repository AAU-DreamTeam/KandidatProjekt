package androidapp.CO2Mad.models.tools.quiz

import kotlin.math.roundToInt

class EmissionKilometersVariant(emission: Double, questionType: QuestionType): QuestionVariant {
    private val emissionPerKM = getEffectPerUnit(questionType)
    override val actualValue = (emission/ emissionPerKM).roundToInt()
    override val roundToNearest = 5
    override val quizValue = calcQuizValue()
    override val quizEffect = quizValue * emissionPerKM
    override val iconStr = " km"
    override var hasBeenAsked = false
    override val actualValueStr get() = if (!hasBeenAsked) valueToString(-1) else valueToString(actualValue)
    override val quizValueStr = valueToString(quizValue)

    private fun valueToString(value: Int): String {
        val unit = "km"

        return if(value == -1) "? $unit" else "$value $unit"
    }

}