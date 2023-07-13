package com.example.learnahead_prototyp.UI.LearningCategory.Test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Test
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.ITestRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HiltViewModel class that represents the ViewModel for the LearningCategories feature.
 * @param repository an implementation of ILearningCategoryRepository for the LearningCategories feature.
 */
@HiltViewModel
class TestViewModel @Inject constructor(
    val repository: ITestRepository
) : ViewModel() {

    /**
     * MutableLiveData that holds the current state of the list of LearningCategories.
     */
    private val _tests = MutableLiveData<UiState<List<Test>>>()
    val test: LiveData<UiState<List<Test>>>
        get() = _tests

    private val _currentTest = MutableLiveData<Test?>()
    val currentTest: LiveData<Test?>
        get() = _currentTest

    fun setCurrentTest(test: Test?) {
        _currentTest.value = test
    }

    /**
     * MutableLiveData that holds the current state of adding a new LearningCategory.
     */
    private val _addTest = MutableLiveData<UiState<Test?>>()
    val addTest: LiveData<UiState<Test?>>
        get() = _addTest

    /**
     * MutableLiveData that holds the current state of updating a LearningCategory.
     */
    private val _updateTest = MutableLiveData<UiState<Test?>>()
    val updateTest: LiveData<UiState<Test?>>
        get() = _updateTest

    /**
     * MutableLiveData that holds the current state of deleting a LearningCategory.
     */
    private val _deleteTest = MutableLiveData<UiState<String>>()
    val deleteTest: LiveData<UiState<String>>
        get() = _deleteTest

    /**
     * Calls the repository to get the list of LearningCategories for the given User and updates the _learningCategories MutableLiveData.
     * @param user the User for which to retrieve the LearningCategories.
     */
    fun getTest(user: User?, learningCategory: LearningCategory) {
        _tests.value = UiState.Loading
        repository.getTests(user, learningCategory) { _tests.value = it }
    }

    /**
     * Calls the repository to add a new LearningCategory and updates the _addLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to add.
     */
    fun addTest(test: Test) {
        _addTest.value = UiState.Loading
        repository.addTest(test) { _addTest.value = it }
    }

    /**
     * Calls the repository to update a LearningCategory and updates the _updateLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to update.
     */
    fun updateTest(test: Test) {
        _updateTest.value = UiState.Loading
        repository.updateTest(test) { _updateTest.value = it }
    }

    /**
     * Calls the repository to delete a LearningCategory and updates the _deleteLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to delete.
     */
    fun deleteTest(test: Test) {
        _deleteTest.value = UiState.Loading
        repository.deleteTest(test) { _deleteTest.value = it }
    }
}