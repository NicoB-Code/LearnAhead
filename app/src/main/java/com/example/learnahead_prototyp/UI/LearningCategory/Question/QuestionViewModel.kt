package com.example.learnahead_prototyp.UI.LearningCategory.Question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.IQuestionRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HiltViewModel class that represents the ViewModel for the LearningCategories feature.
 * @param repository an implementation of ILearningCategoryRepository for the LearningCategories feature.
 */
@HiltViewModel
class QuestionViewModel @Inject constructor(
    val repository: IQuestionRepository
) : ViewModel() {

    /**
     * MutableLiveData that holds the current state of the list of LearningCategories.
     */
    private val _questions = MutableLiveData<UiState<List<Question>>>()
    val question: LiveData<UiState<List<Question>>>
        get() = _questions

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?>
        get() = _currentQuestion

    fun setCurrentQuestion(question: Question?) {
        _currentQuestion.value = question
    }

    /**
     * MutableLiveData that holds the current state of adding a new LearningCategory.
     */
    private val _addQuestion = MutableLiveData<UiState<Question?>>()
    val addQuestion: LiveData<UiState<Question?>>
        get() = _addQuestion

    /**
     * MutableLiveData that holds the current state of updating a LearningCategory.
     */
    private val _updateQuestion = MutableLiveData<UiState<Question?>>()
    val updateQuestion: LiveData<UiState<Question?>>
        get() = _updateQuestion

    /**
     * MutableLiveData that holds the current state of deleting a LearningCategory.
     */
    private val _deleteQuestion = MutableLiveData<UiState<String>>()
    val deleteQuestion: LiveData<UiState<String>>
        get() = _deleteQuestion

    /**
     * Calls the repository to get the list of LearningCategories for the given User and updates the _learningCategories MutableLiveData.
     * @param user the User for which to retrieve the LearningCategories.
     */
    fun getQuestions(user: User?, learningCategory: LearningCategory) {
        _questions.value = UiState.Loading
        repository.getQuestions(user, learningCategory) { _questions.value = it }
    }

    /**
     * Calls the repository to add a new LearningCategory and updates the _addLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to add.
     */
    fun addQuestion(question: Question) {
        _addQuestion.value = UiState.Loading
        repository.addQuestion(question) { _addQuestion.value = it }
    }

    /**
     * Calls the repository to update a LearningCategory and updates the _updateLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to update.
     */
    fun updateQuestion(question: Question) {
        _updateQuestion.value = UiState.Loading
        repository.updateQuestion(question) { _updateQuestion.value = it }
    }

    /**
     * Calls the repository to delete a LearningCategory and updates the _deleteLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to delete.
     */
    fun deleteQuestion(question: Question) {
        _deleteQuestion.value = UiState.Loading
        repository.deleteQuestion(question) { _deleteQuestion.value = it }
    }
}