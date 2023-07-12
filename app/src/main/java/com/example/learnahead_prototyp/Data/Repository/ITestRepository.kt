package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Test
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

interface ITestRepository {
    fun getTests(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Test>>) -> Unit)

    fun addTest(test: Test, result: (UiState<Test?>) -> Unit)

    fun updateTest(test: Test, result: (UiState<Test?>) -> Unit)

    fun deleteTest(test: Test, result: (UiState<String>) -> Unit)
}