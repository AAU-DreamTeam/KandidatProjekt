package androidapp.CO2Mad.models.tools.quiz

class QuestionFactory {
    fun getQuestion(type: QuestionType, emission: Double) : Question {
        when(type) {
            QuestionType.CAR -> return CarQuestion(emission, type)
            QuestionType.PLANE -> return PlaneQuestion(emission, type)
            QuestionType.TRAIN -> return TrainQuestion(emission, type)
            QuestionType.TREE -> return TreeQuestion(emission, type)
            QuestionType.FART -> return  FartQuestion(emission,type)
            else -> throw IllegalArgumentException("Unable to recognize type of question.")
        }
    }
}