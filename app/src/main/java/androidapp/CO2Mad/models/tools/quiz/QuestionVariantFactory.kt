package androidapp.CO2Mad.models.tools.quiz


class QuestionVariantFactory {
    fun getQuestionVariant(variantType: QuestionVariantType, emission: Double, questionType: QuestionType): QuestionVariant {
        return when(variantType) {
            QuestionVariantType.EMISSION_KILOMETERS -> EmissionKilometersVariant(emission, questionType)
            QuestionVariantType.EMISSION_HOURS -> EmissionHoursVariant(emission, questionType)
            QuestionVariantType.ABSORPTION_DAYS -> AbsorptionDaysVariant(emission, questionType)
            QuestionVariantType.FART_AMOUNT -> FartAmountVariant(emission,questionType)
        }
    }
}