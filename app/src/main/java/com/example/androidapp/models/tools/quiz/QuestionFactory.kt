package com.example.androidapp.models.tools.quiz

class QuestionFactory {
    fun getQuestion(type: QuestionType, emission: Double) : Question {
        when(type) {
            QuestionType.CAR -> return CarQuestion(emission)
            QuestionType.PLANE -> return PlaneQuestion(emission)
            QuestionType.TRAIN -> return TrainQuestion(emission)
            QuestionType.TREE -> return TreeQuestion(emission)
            else -> throw IllegalArgumentException("Unable to recognize type of question.")
        }
    }
}