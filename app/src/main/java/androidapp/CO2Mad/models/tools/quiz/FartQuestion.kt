package androidapp.CO2Mad.models.tools.quiz

import androidapp.CO2Mad.R

class FartQuestion(emission: Double, type: QuestionType): Question {
    override val iconId = R.drawable.ic_fart
    override val bgImageId = R.mipmap.ic_farts_foreground
    override val variants = mutableListOf<QuestionVariant>()
    override val variantTypeToIndex = mutableMapOf<QuestionVariantType, Int>()
    override val indices: MutableList<Int> = mutableListOf<Int>()
    override val numberOfVariants: Int
    override var currentIndex = -1

    private val quizValueStr: String get() = variants[currentIndex].quizValueStr
    private val actualValueStr: String get() = variants[currentIndex].actualValueStr
    private val quizAbsorption: Double get() = variants[currentIndex].quizEffect

    init {
        val qvFactory = QuestionVariantFactory()
        val variantTypes = mutableListOf(QuestionVariantType.FART_AMOUNT)

        for ((index, variantType) in variantTypes.withIndex()) {
            variants.add(index, qvFactory.getQuestionVariant(variantType, emission, type))
            variantTypeToIndex[variantType] = index
            indices.add(index)
        }

        numberOfVariants = indices.size

        indices.shuffle()
    }

    override fun getType(): QuestionType {
        return QuestionType.FART
    }

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Hvor mange prutter svarer"
            2 -> quizValueStr
            3 -> "til?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        return when(line) {
            1 -> "En prut svarer til ${"%.1f ".format(quizAbsorption).replace('.', ',')} kg $CO2Str"
            2 -> "Din udledning svarer til ${actualValueStr}s $CO2Str optag"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
    }
}