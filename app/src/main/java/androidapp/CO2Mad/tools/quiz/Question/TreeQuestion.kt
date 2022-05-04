package androidapp.CO2Mad.tools.quiz.Question

import androidapp.CO2Mad.R
import androidapp.CO2Mad.tools.quiz.QuestionVariant.QuestionVariant
import androidapp.CO2Mad.tools.quiz.QuestionVariant.QuestionVariantFactory
import androidapp.CO2Mad.tools.quiz.QuestionVariant.QuestionVariantType

class TreeQuestion(emission: Double, type: QuestionType): Question {
    override val iconId = R.drawable.ic_park_black_24dp
    override val bgImageId = R.mipmap.ic_tree_foreground
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
        val variantTypes = mutableListOf(QuestionVariantType.ABSORPTION_DAYS)

        for ((index, variantType) in variantTypes.withIndex()) {
            variants.add(index, qvFactory.getQuestionVariant(variantType, emission, type))
            variantTypeToIndex[variantType] = index
            indices.add(index)
        }

        numberOfVariants = indices.size

        indices.shuffle()
    }

    override fun getType(): QuestionType {
        return QuestionType.TREE
    }

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Bruger et træ"
            2 -> quizValueStr
            3 -> "for at optage din udledning?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        return when(line) {
            1 -> "Et træ optager ${"%.1f ".format(quizAbsorption).replace('.', ',')} kg $CO2Str på $quizValueStr"
            2 -> "Din udledning svarer til ${actualValueStr}s $CO2Str optag"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
    }
}