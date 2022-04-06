package com.example.androidapp.models.tools.quiz

interface Question {
    val CO2Str: String get() = "CO<sub><small><small>2</small></small></sub>"
    val iconId: Int
    val bgImageId: Int
    val variants: MutableList<QuestionVariant>
    val variantTypeToIndex: MutableMap<QuestionVariantType, Int>
    val indices: MutableList<Int>
    val numberOfVariants: Int
    var currentIndex: Int

    fun submit(answer: QuestionAnswer): Boolean? {
        return variants[currentIndex].submit(answer)
    }

    fun draw(){
        currentIndex = indices.removeLast()
    }

    fun getVariant(variantType: QuestionVariantType): QuestionVariant {
        val variantIndex = variantTypeToIndex[variantType]

        if (variantIndex != null) {
            return variants[variantIndex]
        } else {
            throw IllegalArgumentException("Unable to find variant of type ${variantType.name}.")
        }
    }

    fun getType(): QuestionType
    fun showQuestion(){
        for (variant in variants){
            variant.hasBeenAsked = true
        }
    }

    fun getQuestionLine(line: Int): String

    fun getAnswerLine(line: Int): String
}