package androidapp.CO2Mad.models.tools.quiz

import kotlin.random.Random

interface QuestionVariant {
    val actualValue: Int
    val quizValue: Int
    val quizEffect: Double
    val actualValueStr: String
    val quizValueStr: String
    val roundToNearest: Int
    val iconStr: String
    var hasBeenAsked: Boolean

    fun submit(answer: QuestionAnswer): Boolean {
        hasBeenAsked = true
        return when(answer){
            QuestionAnswer.ABOVE -> quizValue < actualValue
            QuestionAnswer.BELLOW -> quizValue > actualValue
        }
    }

    fun getEffectPerUnit(questionType: QuestionType): Double {
        return when(questionType) {
            QuestionType.CAR -> 0.171
            QuestionType.PLANE -> 0.254
            QuestionType.TRAIN -> 0.041
            QuestionType.TREE -> 0.060
        }
    }
    fun iconStr():String{
        if(hasBeenAsked){
            return actualValueStr
        }
        return "?$iconStr"
    }

    fun QuestionVariant.calcQuizValue() : Int {
        var value: Int

        if(actualValue >= 1){
            do {
                value = ((Random.nextInt(1, 2 * actualValue) + roundToNearest) / roundToNearest) * roundToNearest
            } while (value == actualValue)

        }else{
            value = 0
        }
        return value
    }
}