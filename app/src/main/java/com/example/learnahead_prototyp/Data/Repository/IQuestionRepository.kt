package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

interface IQuestionRepository {

    fun getQuestions(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Question>>) -> Unit)

    fun addQuestion(question: Question, result: (UiState<Question?>) -> Unit)

    fun updateQuestion(question: Question, result: (UiState<Question?>) -> Unit)

    fun deleteQuestion(question: Question, result: (UiState<String>) -> Unit)
}