package com.example.learnahead_prototyp.UI.Goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.ISummaryRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HiltViewModel class that represents the ViewModel for the LearningCategories feature.
 * @param repository an implementation of ILearningCategoryRepository for the LearningCategories feature.
 */
@HiltViewModel
class SummaryViewModel @Inject constructor(
    val repository: ISummaryRepository
) : ViewModel() {

    /**
     * MutableLiveData that holds the current state of the list of LearningCategories.
     */
    private val _summaries = MutableLiveData<UiState<List<Summary>>>()
    val summary: LiveData<UiState<List<Summary>>>
        get() = _summaries

    private val _currentLearningCategory = MutableLiveData<UiState<LearningCategory>>()
    val currentLearningCategory: LiveData<UiState<LearningCategory>>
        get() = _currentLearningCategory

    /**
     * MutableLiveData that holds the current state of adding a new LearningCategory.
     */
    private val _addSummary = MutableLiveData<UiState<Summary?>>()
    val addSummary: LiveData<UiState<Summary?>>
        get() = _addSummary

    /**
     * MutableLiveData that holds the current state of updating a LearningCategory.
     */
    private val _updateSummary = MutableLiveData<UiState<Summary?>>()
    val updateSummary: LiveData<UiState<Summary?>>
        get() = _updateSummary

    /**
     * MutableLiveData that holds the current state of deleting a LearningCategory.
     */
    private val _deleteSummary = MutableLiveData<UiState<String>>()
    val deleteSummary: LiveData<UiState<String>>
        get() = _deleteSummary

    /**
     * Calls the repository to get the list of LearningCategories for the given User and updates the _learningCategories MutableLiveData.
     * @param user the User for which to retrieve the LearningCategories.
     */
    fun getSummaries(user: User?, learningCategory: LearningCategory) {
        _summaries.value = UiState.Loading
        repository.getSummaries(user, learningCategory) { _summaries.value = it }
    }

    /**
     * Calls the repository to add a new LearningCategory and updates the _addLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to add.
     */
    fun addSummary(summary: Summary) {
        _addSummary.value = UiState.Loading
        repository.addSummary(summary) { _addSummary.value = it }
    }

    /**
     * Calls the repository to update a LearningCategory and updates the _updateLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to update.
     */
    fun updateSummary(summary: Summary) {
        _updateSummary.value = UiState.Loading
        repository.updateSummary(summary) { _updateSummary.value = it }
    }

    /**
     * Calls the repository to delete a LearningCategory and updates the _deleteLearningCategory MutableLiveData.
     * @param learningCategory the LearningCategory to delete.
     */
    fun deleteSummary(summary: Summary) {
        _deleteSummary.value = UiState.Loading
        repository.deleteSummary(summary) { _deleteSummary.value = it }
    }
}