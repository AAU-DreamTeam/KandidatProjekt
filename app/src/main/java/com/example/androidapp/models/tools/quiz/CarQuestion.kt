package com.example.androidapp.models.tools.quiz

import com.example.androidapp.R

class CarQuestion(emission: Double, type: QuestionType): Question {
    var result: Boolean?= null
    override val iconId = R.drawable.ic_directions_car_black_24dp
    override val bgImageId = R.mipmap.ic_car_foreground
    override val variants = mutableListOf<QuestionVariant>()
    override val variantTypeToIndex = mutableMapOf<QuestionVariantType, Int>()
    override val numberOfVariants: Int
    override var currentIndex = -1
    override val indices = mutableListOf<Int>()

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

    override fun getQuestionLine(line: Int): String {
        return when(line) {
            1 -> "Kører en bil"
            2 -> quizValueStr
            3 -> "for at matche din udledning?"
            else -> throw Exception("Unable to generate question line: $line.")
        }
    }

    override fun getAnswerLine(line: Int): String {
        return when(line) {
            1 -> "En bil udleder ${"%.1f ".format(quizEmission).replace('.', ',')} kg $CO2Str ved at køre $quizValueStr"
            2 -> "Din udledning svarer til at køre $actualValueStr"
            else -> throw Exception("Unable to generate answer line: $line.")
        }
    }

}