package androidapp.CO2Mad.models.tools.quiz

import kotlin.math.roundToInt

class FartAmountVariant(emission: Double, questionType: QuestionType): QuestionVariant {
    private val absorptionPerFart = getEffectPerUnit(questionType)
    override val roundToNearest = 5
    override val actualValue = (emission / absorptionPerFart).roundToInt()
    override val quizValue = calcQuizValue()
    override var hasBeenAsked = false
    override val iconStr = " prut"
    override val actualValueStr get() = if (!hasBeenAsked) valueToString(-1) else valueToString(actualValue)
    override val quizValueStr = valueToString(quizValue)
    override val quizEffect = quizValue * absorptionPerFart

    private fun valueToString(value: Int): String {
        val unitSingular = "prut"
        val unitPlural = "prutter"

        return if(value == -1) "? $unitPlural" else "$value ${if (value == 1) unitSingular else unitPlural}"
    }
}