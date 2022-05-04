package androidapp.CO2Mad.tools.quiz.QuestionVariant

import androidapp.CO2Mad.tools.quiz.Question.QuestionType


class QuestionVariantFactory {
    fun getQuestionVariant(variantType: QuestionVariantType, emission: Double, questionType: QuestionType): QuestionVariant {
        return when(variantType) {
            QuestionVariantType.EMISSION_KILOMETERS -> EmissionKilometersVariant(emission, questionType)
            QuestionVariantType.EMISSION_HOURS -> EmissionTimeVariant(emission, questionType)
            QuestionVariantType.ABSORPTION_DAYS -> AbsorptionDaysVariant(emission, questionType)
        }
    }
}