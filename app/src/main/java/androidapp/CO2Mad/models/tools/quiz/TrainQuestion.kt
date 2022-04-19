package androidapp.CO2Mad.models.tools.quiz

import androidapp.CO2Mad.R

class TrainQuestion(emission: Double, type: QuestionType): Question {
    override val iconId = R.drawable.ic_train_solid
    override val bgImageId = R.mipmap.ic_train_foreground
    override val variants = mutableListOf<QuestionVariant>()
    override val variantTypeToIndex = mutableMapOf<QuestionVariantType, Int>()
    override val indices = mutableListOf<Int>()
    override val numberOfVariants: Int
    override var currentIndex = -1

    private val quizValueStr: String get() = variants[currentIndex].quizValueStr
    private val actualValueStr: String get() = variants[currentIndex].actualValueStr
    private val quizEmission: Double get() = variants[currentIndex].quizEffect

    init {
        val qvFactory = QuestionVariantFactory()
        val variantTypes = mutableListOf(QuestionVariantType.EMISSION_KILOMETERS, QuestionVariantType.EMISSION_HOURS)

        for ((index, variantType) in variantTypes.withIndex()) {
            variants.add(index, qvFactory.getQuestionVariant(variantType, emission, type))
            variantTypeToIndex[variantType] = index
            indices.add(index)
        }

        numberOfVariants = indices.size

        indices.shuffle()
    }

    override fun getType(): QuestionType {
        return QuestionType.TRAIN
    }

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Kører en togpassager"
            2 -> quizValueStr
            3 -> "for at matche din udledning?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        return when(line) {
            1 -> "En togpassager udleder ${"%.1f ".format(quizEmission).replace('.', ',')} kg $CO2Str ved at køre $quizValueStr"
            2 -> "Din udledning svarer til at køre $actualValueStr"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
    }
}