package com.example.learnahead_prototyp.UI.Goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.ILearnCategoryRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HiltViewModel class that represents the ViewModel for the LearningCategories feature.
 * @param repository an implementation of ILearningCategoryRepository for the LearningCategories feature.
 */
@HiltViewModel
class LearnCategoryViewModel @Inject constructor(
    val repository: ILearnCategoryRepository
) : ViewModel() {

    /**
     * MutableLiveData that holds the current state of the list of LearningCategories.
     */
    private val _learningCategories = MutableLiveData<UiState<List<LearningCategory>>>()
    val learningCategory: LiveData<UiState<List<LearningCategory>>>
        get() = _learningCategories

    /**
     * MutableLiveData that holds the current state of adding a new LearningCategory.
     */
    private val _addLearningCategory = MutableLiveData<UiState<String>>()
    val addLearningCategory: LiveData<UiState<String>>
        get() = _addLearningCategory

    /**
     * MutableLiveData that holds the current state of updating a LearningCategory.
     */
    private val _updateLearningCategory = MutableLiveData<UiState<String>>()
    val updateLearningCategory: LiveData<UiState<String>>
        get() = _updateLearningCategory

    /**
     * MutableLiveData that holds the current state of deleting a LearningCategory.
     */
    private val _deleteLearningCategory = MutableLiveData<UiState<String>>()
    val deleteLearningCategory: LiveData<UiState<String>>
        get() = _deleteLearningCategory

    /**
     * Calls the repository to get the list of LearningCategories for the given User and updates the _learningCategories MutableLiveData.
     * @param user the User for which to retrieve the LearningCategories.
     */
    fun getLearningCategories(user: User?) {
        _learningCategories.value = UiState.Loading
        repository.getLearningCategories(user) { _learningCategories.value = it }
    }

    /**
     * Calls the repository to add a new LearningCategory and updates the _addLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to add.
     */
    fun addLearningCategory(learningCategory: LearningCategory) {
        _addLearningCategory.value = UiState.Loading
        repository.addLearningCategory(learningCategory) { _addLearningCategory.value = it }
    }

    /**
     * Calls the repository to update a LearningCategory and updates the _updateLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to update.
     */
    fun updateLearningCategory(learningCategory: LearningCategory) {
        _updateLearningCategory.value = UiState.Loading
        repository.updateLearningCategory(learningCategory) { _updateLearningCategory.value = it }
    }

    /**
     * Calls the repository to delete a LearningCategory and updates the _deleteLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to delete.
     */
    fun deleteLearningCategory(learningCategory: LearningCategory) {
        _deleteLearningCategory.value = UiState.Loading
        repository.deleteLearningCategory(learningCategory) { _deleteLearningCategory.value = it }
    }
}