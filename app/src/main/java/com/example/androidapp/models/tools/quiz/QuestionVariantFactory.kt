package com.example.androidapp.models.tools.quiz


class QuestionVariantFactory {
    fun getQuestionVariant(variantType: QuestionVariantType, emission: Double, questionType: QuestionType): QuestionVariant {
        return when(variantType) {
            QuestionVariantType.EMISSION_KILOMETERS -> EmissionKilometersVariant(emission, questionType)
            QuestionVariantType.EMISSION_HOURS -> EmissionHoursVariant(emission, questionType)
            QuestionVariantType.ABSORPTION_DAYS -> AbsorptionDaysVariant(emission, questionType)
        }
    }
}