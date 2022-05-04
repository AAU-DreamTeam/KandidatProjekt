package androidapp.CO2Mad.tools.quiz.Question

import androidapp.CO2Mad.R
import androidapp.CO2Mad.tools.quiz.QuestionVariant.QuestionVariant
import androidapp.CO2Mad.tools.quiz.QuestionVariant.QuestionVariantFactory
import androidapp.CO2Mad.tools.quiz.QuestionVariant.QuestionVariantType

class PlaneQuestion(emission: Double, type: QuestionType) : Question {
    override val iconId = R.drawable.ic_airplanemode_active_black_24dp
    override val bgImageId = R.mipmap.ic_plane_foreground
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
        val variantTypes = mutableListOf(
            QuestionVariantType.EMISSION_KILOMETERS,
            QuestionVariantType.EMISSION_HOURS
        )

        for ((index, variantType) in variantTypes.withIndex()) {
            variants.add(index, qvFactory.getQuestionVariant(variantType, emission, type))
            variantTypeToIndex[variantType] = index
            indices.add(index)
        }

        numberOfVariants = indices.size

        indices.shuffle()
    }

    override fun getType(): QuestionType {
        return QuestionType.PLANE
    }

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Flyver en flypassager"
            2 -> quizValueStr
            3 -> "for at matche din udledning?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        return when(line) {
            1 -> "En flypassager udleder ${"%.1f ".format(quizEmission).replace('.', ',')} kg $CO2Str ved at flyve $quizValueStr"
            2 -> "Din udledning svarer til at flyve $actualValueStr"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
    }
}